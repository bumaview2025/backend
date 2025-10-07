package bumaview.bumaview.domain.interview.presentation.controller;

import bumaview.bumaview.domain.interview.presentation.dto.req.InterviewAnswerRequestDto;
import bumaview.bumaview.domain.interview.presentation.dto.res.InterviewAnswerResponseDto;
import bumaview.bumaview.domain.interview.presentation.dto.res.InterviewQuestionResponseDto;
import bumaview.bumaview.domain.interview.presentation.dto.res.MyAnswerListResponseDto;
import bumaview.bumaview.domain.interview.service.InterviewService;
import bumaview.bumaview.global.security.user.BumaviewUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewService interviewService;

    @GetMapping
    public ResponseEntity<InterviewQuestionResponseDto> getInterviewQuestion(
            @AuthenticationPrincipal BumaviewUserDetails bumaviewUserDetails) {
        InterviewQuestionResponseDto question = interviewService.getRandomQuestion(bumaviewUserDetails);
        return ResponseEntity.ok(question);
    }

    @PostMapping
    public ResponseEntity<InterviewAnswerResponseDto> submitAnswer(
            @AuthenticationPrincipal BumaviewUserDetails bumaviewUserDetails,
            @RequestBody InterviewAnswerRequestDto request) {
        InterviewAnswerResponseDto answer = interviewService.submitAnswer(bumaviewUserDetails, request);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/answerlist")
    public ResponseEntity<List<MyAnswerListResponseDto>> getMyAnswerList(
            @AuthenticationPrincipal BumaviewUserDetails bumaviewUserDetails) {
        List<MyAnswerListResponseDto> answerList = interviewService.getMyAnswerList(bumaviewUserDetails);
        return ResponseEntity.ok(answerList);
    }

    @PostMapping("/portfolio")
    public ResponseEntity<String> uploadPortfolio(
            @AuthenticationPrincipal BumaviewUserDetails bumaviewUserDetails,
            @RequestParam("file") MultipartFile file) {
        String fileName = interviewService.uploadPortfolio(bumaviewUserDetails, file);
        return ResponseEntity.ok(fileName);
    }

}