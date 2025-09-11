package bumaview.bumaview.domain.oauth2.infra.client;

import bumaview.bumaview.domain.oauth2.presentation.dto.res.GoogleInformationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleOAuthUserInfo", url = "https://www.googleapis.com")
public interface GoogleOAuth2UserInfoClient {
    @GetMapping(
            value = "/oauth2/v3/userinfo",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    GoogleInformationResponse getUserInformation(@RequestHeader("Authorization") String accessToken);
}
