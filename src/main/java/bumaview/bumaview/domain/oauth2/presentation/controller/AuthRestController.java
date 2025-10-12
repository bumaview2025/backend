package bumaview.bumaview.domain.oauth2.presentation.controller;

import bumaview.bumaview.domain.oauth2.application.service.GoogleOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthRestController {
    private final GoogleOAuth2Service googleOAuth2Service;

    @GetMapping("/auth/link")
    public ResponseEntity<String> getGoogleOauth2Url() {
        String authUrl = googleOAuth2Service.generateAuthUrl();
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<Void> oauth2Callback(@RequestParam String code) {
        String accessToken = googleOAuth2Service.authenticateUser(code);
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:3000?token=" + accessToken)
                .build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> oauth2Login(@RequestParam String code) {
        String accessToken = googleOAuth2Service.authenticateUser(code);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity<Void> oauth2Logout() {
        return ResponseEntity.ok().header("Authorization", "Bearer ").build();
    }
}

