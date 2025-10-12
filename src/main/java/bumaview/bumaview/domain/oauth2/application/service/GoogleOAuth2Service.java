package bumaview.bumaview.domain.oauth2.application.service;

import bumaview.bumaview.domain.oauth2.infra.builder.GoogleOAuth2LinkBuilder;
import bumaview.bumaview.domain.oauth2.infra.client.GoogleOAuth2UserInfoClient;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleInformationResponse;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleTokenResponse;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.global.exception.BumaviewException;
import bumaview.bumaview.global.properties.GoogleOAuth2ProviderProperties;
import bumaview.bumaview.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final GoogleOAuth2LinkBuilder googleOAuth2LinkBuilder;
    private final GoogleOAuth2UserInfoClient googleOAuth2UserInfoClient;
    private final GoogleOAuth2ProviderProperties googleOAuth2ProviderProperties;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    public String generateAuthUrl() {
        return googleOAuth2LinkBuilder.buildUrl();
    }

    @Transactional
    public String authenticateUser(String code) {
        GoogleTokenResponse tokenResponse = getAccessToken(code);
        GoogleInformationResponse userInfo = getUserInfo(tokenResponse.accessToken());
        UserEntity user = saveOrUpdateUser(userInfo);
        return jwtProvider.createAccessToken(user.getUserId().toString());
    }

    private GoogleTokenResponse getAccessToken(String code) {
        String rawCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", rawCode);
        formData.add("client_id", googleOAuth2ProviderProperties.getClientId());
        formData.add("client_secret", googleOAuth2ProviderProperties.getClientSecret());
        formData.add("redirect_uri", googleOAuth2ProviderProperties.getRedirectUri());
        formData.add("grant_type", googleOAuth2ProviderProperties.getGrantType());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        log.info("Requesting Google OAuth2 token with code: {}", rawCode.substring(0, Math.min(10, rawCode.length())) + "...");

        try {
            GoogleTokenResponse response = restTemplate.postForObject(
                    "https://oauth2.googleapis.com/token",
                    request,
                    GoogleTokenResponse.class
            );
            log.info("Successfully received access token from Google");
            return response;
        } catch (HttpClientErrorException e) {
            log.error("Google OAuth2 token request failed - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BumaviewException(HttpStatus.UNAUTHORIZED, "OAuth2 인증에 실패했습니다. 인증 코드가 유효하지 않거나 만료되었습니다.");
        } catch (Exception e) {
            log.error("Unexpected error during Google OAuth2 token request", e);
            throw new BumaviewException(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth2 인증 중 오류가 발생했습니다.");
        }
    }

    private GoogleInformationResponse getUserInfo(String accessToken) {
        return googleOAuth2UserInfoClient.getUserInformation("Bearer " + accessToken);
    }

    private UserEntity saveOrUpdateUser(GoogleInformationResponse userInfo) {
        return userRepository.findBySub(userInfo.sub())
                .orElseGet(() -> createNewUser(userInfo));
    }

    private UserEntity createNewUser(GoogleInformationResponse userInfo) {
        UserEntity newUser = UserEntity.builder()
                .username(userInfo.name())
                .sub(userInfo.sub())
                .email(userInfo.email())
                .profileImageUrl(userInfo.picture())
                .signupBuilder();
        return userRepository.save(newUser);
    }
}
