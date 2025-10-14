package bumaview.bumaview.domain.user.presentation.controller;

import bumaview.bumaview.domain.user.application.service.UserService;
import bumaview.bumaview.domain.user.domain.entity.DreamJob;
import bumaview.bumaview.domain.user.presentation.dto.UserInfoRequestDto;
import bumaview.bumaview.domain.user.presentation.dto.UserInfoResponseDto;
import bumaview.bumaview.global.security.user.BumaviewUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal BumaviewUserDetails bumaviewUserDetails) {
        log.info("Getting user info");
        UserInfoResponseDto response = userService.getUserInfo(bumaviewUserDetails);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserInfoResponseDto> saveUserInfoWithFile(
            @AuthenticationPrincipal BumaviewUserDetails bumaviewUserDetails,
            @RequestParam("username") String username,
            @RequestParam("dream_job") DreamJob dreamJob,
            @RequestParam(value = "portfolio", required = false) MultipartFile portfolio,
            @RequestParam("github_repository") String githubRepository) {

        log.info("Received user info request (multipart) - username: {}, dreamJob: {}, githubRepository: {}",
                username, dreamJob, githubRepository);

        UserInfoRequestDto requestDto = new UserInfoRequestDto(
                username,
                dreamJob,
                portfolio,
                githubRepository
        );

        UserInfoResponseDto response = userService.saveUserInfo(bumaviewUserDetails, requestDto);

        log.info("User info saved successfully for user: {}", response.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}