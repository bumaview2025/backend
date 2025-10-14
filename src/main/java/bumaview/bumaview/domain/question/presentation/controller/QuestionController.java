package bumaview.bumaview.domain.question.presentation.controller;

import bumaview.bumaview.domain.question.presentation.dto.req.QuestionCreateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.req.QuestionUpdateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.res.QuestionResponseDto;
import bumaview.bumaview.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<Void> createQuestion(@RequestBody QuestionCreateRequestDto request) {
        questionService.createQuestion(request);
        return ResponseEntity.ok().build();
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