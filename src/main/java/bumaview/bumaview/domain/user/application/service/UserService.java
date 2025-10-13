package bumaview.bumaview.domain.user.application.service;

import bumaview.bumaview.domain.user.service.PdfToMarkdownConverter;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.domain.user.presentation.dto.UserInfoRequestDto;
import bumaview.bumaview.domain.user.presentation.dto.UserInfoResponseDto;
import bumaview.bumaview.domain.user.presentation.dto.UserMetaDataResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PdfToMarkdownConverter pdfToMarkdownConverter;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String METADATA_API_URL = "http://localhost:8000/api/user-metadata";

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        log.info("Getting user info for: {}", principalName);

        UserEntity user;
        // Try to parse as userId first
        try {
            Long userId = Long.parseLong(principalName);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        } catch (NumberFormatException e) {
            // If not a number, try to find by username
            log.info("Token subject is not a userId, trying to find by username: {}", principalName);
            user = userRepository.findByUsername(principalName)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        }

        return UserInfoResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .dreamJob(user.getDreamJob())
                .portfolio(user.getPortfolio())
                .githubRepository(user.getGithubRepository())
                .userMetaData(parseJson(user.getUserMetaData()))
                .githubInfo(parseJson(user.getGithubInfo()))
                .message("사용자 정보를 성공적으로 조회했습니다.")
                .build();
    }

    @Transactional
    public UserInfoResponseDto saveUserInfo(UserInfoRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        log.info("Authentication name from token: {}", principalName);

        UserEntity user;
        // Try to parse as userId first
        try {
            Long userId = Long.parseLong(principalName);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        } catch (NumberFormatException e) {
            // If not a number, try to find by username
            log.info("Token subject is not a userId, trying to find by username: {}", principalName);
            user = userRepository.findByUsername(principalName)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        }

        String portfolioMd = null;
        if (requestDto.getPortfolio() != null && !requestDto.getPortfolio().isEmpty()) {
            if (pdfToMarkdownConverter.isPdfFile(requestDto.getPortfolio())) {
                portfolioMd = pdfToMarkdownConverter.convertPdfToMarkdown(requestDto.getPortfolio());
                log.info("Portfolio converted to Markdown: {} characters", portfolioMd.length());
            } else {
                throw new RuntimeException("Portfolio 파일은 PDF 형식이어야 합니다.");
            }
        }

        UserMetaDataResponseDto metaDataResponse = callMetaDataApi(portfolioMd, requestDto.getGithubRepository());

        String userMetaDataJson = metaDataResponse.getMetadata();
        String githubInfoJson = convertToJson(metaDataResponse.getGithubData());

        user.updateProfile(
                requestDto.getUsername(),
                requestDto.getDreamJob(),
                portfolioMd,
                requestDto.getGithubRepository(),
                userMetaDataJson,
                githubInfoJson
        );

        UserEntity savedUser = userRepository.save(user);

        return UserInfoResponseDto.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .dreamJob(savedUser.getDreamJob())
                .portfolio(savedUser.getPortfolio())
                .githubRepository(savedUser.getGithubRepository())
                .userMetaData(parseJson(savedUser.getUserMetaData()))
                .githubInfo(parseJson(savedUser.getGithubInfo()))
                .message("사용자 정보가 성공적으로 저장되었습니다.")
                .build();
    }

    private UserMetaDataResponseDto callMetaDataApi(String portfolioMd, String githubRepository) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("portfolio", portfolioMd);
            requestBody.put("github_repository", githubRepository);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            log.info("Calling metadata API: {}", METADATA_API_URL);
            UserMetaDataResponseDto response = restTemplate.postForObject(
                    METADATA_API_URL,
                    requestEntity,
                    UserMetaDataResponseDto.class
            );

            if (response == null) {
                throw new RuntimeException("메타데이터 API로부터 응답을 받지 못했습니다.");
            }

            log.info("Successfully received metadata from API");
            return response;

        } catch (Exception e) {
            log.error("Failed to call metadata API", e);
            throw new RuntimeException("메타데이터 API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
            throw new RuntimeException("JSON 변환 중 오류가 발생했습니다.", e);
        }
    }

    private JsonNode parseJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON", e);
            return null;
        }
    }
}