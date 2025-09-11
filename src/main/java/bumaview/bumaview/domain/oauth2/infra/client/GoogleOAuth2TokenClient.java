package bumaview.bumaview.domain.oauth2.infra.client;

import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleTokenResponse;
import bumaview.bumaview.domain.oauth2.presentation.dto.req.GoogleTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "googleOAuthToken", url = "https://oauth2.googleapis.com")
public interface GoogleOAuth2TokenClient {
    @PostMapping(
            value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    GoogleTokenResponse getAccessToken(@RequestBody GoogleTokenRequest googleTokenRequest);
}
