package bumaview.bumaview.domain.oauth2.application.service;

import bumaview.bumaview.domain.oauth2.infra.builder.GoogleOAuth2LinkBuilder;
import bumaview.bumaview.domain.oauth2.infra.client.GoogleOAuth2TokenClient;
import bumaview.bumaview.domain.oauth2.infra.client.GoogleOAuth2UserInfoClient;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleInformationResponse;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleTokenResponse;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.global.properties.GoogleOAuth2ProviderProperties;
import bumaview.bumaview.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final GoogleOAuth2LinkBuilder googleOAuth2LinkBuilder;
    private final GoogleOAuth2TokenClient googleOAuth2TokenClient;
    private final GoogleOAuth2UserInfoClient googleOAuth2UserInfoClient;
    private final GoogleOAuth2ProviderProperties googleOAuth2ProviderProperties;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

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
        
        Map<String, String> formData = Map.of(
                "code", rawCode,
                "client_id", googleOAuth2ProviderProperties.getClientId(),
                "client_secret", googleOAuth2ProviderProperties.getClientSecret(),
                "redirect_uri", googleOAuth2ProviderProperties.getRedirectUri(),
                "grant_type", googleOAuth2ProviderProperties.getGrantType()
        );
        
        return googleOAuth2TokenClient.getAccessToken(formData);
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
                .signupBuilder();
        return userRepository.save(newUser);
    }
}
