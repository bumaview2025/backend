package bumaview.bumaview.domain.oauth2.presentation.controller;

import bumaview.bumaview.domain.oauth2.application.service.GoogleOAuth2Service;
import bumaview.bumaview.domain.oauth2.presentation.dto.res.UserResponseDto;
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
    public ResponseEntity<UserResponseDto> oauth2Login(@RequestParam String code) {
        UserResponseDto userResponseDto = googleOAuth2Service.authenticateUser(code);
        return ResponseEntity.ok(userResponseDto);
    }
}
