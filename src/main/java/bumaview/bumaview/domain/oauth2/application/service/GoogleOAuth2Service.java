package bumaview.bumaview.domain.oauth2.application.service;

import bumaview.bumaview.domain.oauth2.infra.builder.GoogleOAuth2LinkBuilder;
import bumaview.bumaview.domain.oauth2.infra.client.GoogleOAuth2TokenClient;
import bumaview.bumaview.domain.oauth2.infra.client.GoogleOAuth2UserInfoClient;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleInformationResponse;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleTokenResponse;
import bumaview.bumaview.domain.oauth2.presentation.dto.req.GoogleTokenRequest;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.UserResponseDto;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.global.properties.GoogleOAuth2ProviderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final GoogleOAuth2LinkBuilder googleOAuth2LinkBuilder;
    private final GoogleOAuth2TokenClient googleOAuth2TokenClient;
    private final GoogleOAuth2UserInfoClient googleOAuth2UserInfoClient;
    private final GoogleOAuth2ProviderProperties googleOAuth2ProviderProperties;
    private final UserRepository userRepository;

    public String generateAuthUrl() {
        return googleOAuth2LinkBuilder.buildUrl();
    }

    @Transactional
    public UserResponseDto authenticateUser(String code) {
        GoogleTokenResponse tokenResponse = getAccessToken(code);
        GoogleInformationResponse userInfo = getUserInfo(tokenResponse.accessToken());
        return UserResponseDto.from(saveOrUpdateUser(userInfo));
    }

    private GoogleTokenResponse getAccessToken(String code) {
        GoogleTokenRequest tokenRequest = new GoogleTokenRequest(
                code,
                googleOAuth2ProviderProperties.getClientId(),
                googleOAuth2ProviderProperties.getClientSecret(),
                googleOAuth2ProviderProperties.getRedirectUrl(),
                "authorization_code"
        );
        return googleOAuth2TokenClient.getAccessToken(tokenRequest);
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
                .signupBuilder();
        return userRepository.save(newUser);
    }
}
