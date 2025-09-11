package bumaview.bumaview.global.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleOAuth2ProviderProperties {
    private String baseUrl;
    private String clientId;
    private String redirectUrl;
    private String clientSecret;
    private String grantType;
}
