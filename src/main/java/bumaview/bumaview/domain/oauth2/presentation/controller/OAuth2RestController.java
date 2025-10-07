package bumaview.bumaview.domain.oauth2.presentation.controller;

import bumaview.bumaview.domain.oauth2.application.service.GoogleOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2RestController {
    private final GoogleOAuth2Service googleOAuth2Service;

    @GetMapping("/link")
    public ResponseEntity<String> getGoogleOauth2Url() {
        String authUrl = googleOAuth2Service.generateAuthUrl();
        return ResponseEntity.ok(authUrl);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> oauth2Login(@RequestParam String code) {
        String accessToken = googleOAuth2Service.authenticateUser(code);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> oauth2Logout() {
        return ResponseEntity.ok().header("Authorization", "Bearer ").build();
    }
}
