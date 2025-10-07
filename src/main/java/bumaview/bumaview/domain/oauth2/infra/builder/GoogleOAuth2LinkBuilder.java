package bumaview.bumaview.domain.oauth2.infra.builder;

import bumaview.bumaview.global.properties.GoogleOAuth2ProviderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2LinkBuilder {
    private final GoogleOAuth2ProviderProperties googleOAuth2ProviderProperties;

    public String buildUrl() {
        return googleOAuth2ProviderProperties.getBaseUrl() +
                "?client_id=" + googleOAuth2ProviderProperties.getClientId() +
                "&redirect_uri=" + googleOAuth2ProviderProperties.getRedirectUri() +
                "&response_type=code" +
                "&scope=openid%20email%20profile";
    }
}
