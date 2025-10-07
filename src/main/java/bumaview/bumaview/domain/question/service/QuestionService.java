package bumaview.bumaview.domain.question.service;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import bumaview.bumaview.domain.question.domain.repository.QuestionRepository;
import bumaview.bumaview.domain.question.presentation.dto.req.QuestionCreateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.req.QuestionUpdateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.res.QuestionResponseDto;
import bumaview.bumaview.domain.question.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<QuestionResponseDto> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(QuestionResponseDto::from)
                .toList();
    }

    public List<QuestionResponseDto> searchQuestions(String column, String query) {
        return questionRepository.findByColumnAndQuery(column, query)
                .stream()
                .map(QuestionResponseDto::from)
                .toList();
    }

    @Transactional
    public void createQuestion(QuestionCreateRequestDto request) {
        QuestionEntity question = QuestionEntity.builder()
                .question(request.question())
                .category(request.category())
                .company(request.company())
                .questionAt(request.questionAt())
                .build();
        
        questionRepository.save(question);
    }

    @Transactional
    public void updateQuestion(QuestionUpdateRequestDto request) {
         QuestionEntity question = questionRepository.findById(request.id())
                .orElseThrow(() -> new QuestionNotFoundException("해당 아이디 값이 존재하지 않습니다."));

         question.updateQuestion(request.question(), request.category(), request.company(), request.questionAt());

        questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new QuestionNotFoundException("해당 아이디 값이 존재하지 않습니다.");
        }
        questionRepository.deleteById(id);
    }

    @Transactional
    public void uploadQuestionFile(@SuppressWarnings("unused") MultipartFile file) {
        // TODO: Implement file upload logic
        // Process file parameter when implementation is ready
    }

    public byte[] downloadSampleFile() {
        // TODO: Implement sample file download logic  
        return new byte[0];
    }
}