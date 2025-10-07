package bumaview.bumaview.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleOAuth2ProviderProperties {
    private final String baseUrl;
    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;
    private final String grantType;
}
