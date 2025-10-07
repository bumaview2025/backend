package bumaview.bumaview.domain.oauth2.infra.client;

import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "googleOAuthToken", url = "https://oauth2.googleapis.com")
public interface GoogleOAuth2TokenClient {

    @PostMapping(value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GoogleTokenResponse getAccessToken(@RequestBody Map<String, String> formData);
}