package bumaview.bumaview.domain.question.presentation.controller;

import bumaview.bumaview.domain.question.presentation.dto.req.QuestionCreateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.req.QuestionUpdateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.res.QuestionResponseDto;
import bumaview.bumaview.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getQuestions(
            @RequestParam(required = false) String column,
            @RequestParam(required = false) String query) {
        
        List<QuestionResponseDto> questions;
        
        if (column != null && query != null) {
            questions = questionService.searchQuestions(column, query);
        } else {
            questions = questionService.getAllQuestions();
        }

        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<Long> createQuestion(@RequestBody QuestionCreateRequestDto request) {
        log.info("Creating question - question: {}, category: {}, company: {}, questionAt: {}",
                request.question(), request.category(), request.company(), request.questionAt());
        Long questionId = questionService.createQuestion(request);
        log.info("Question created successfully with id: {}", questionId);
        return ResponseEntity.ok(questionId);
    }

    @PatchMapping
    public ResponseEntity<Void> updateQuestion(@RequestBody QuestionUpdateRequestDto request) {
        questionService.updateQuestion(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteQuestion(@RequestParam Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadQuestionFile(@RequestParam("file") MultipartFile file) {
        questionService.uploadQuestionFile(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sample")
    public ResponseEntity<byte[]> downloadSampleFile() {
        byte[] fileData = questionService.downloadSampleFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "면접질문_샘플.xlsx");

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
}