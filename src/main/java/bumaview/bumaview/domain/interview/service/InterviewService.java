package bumaview.bumaview.domain.interview.service;

import bumaview.bumaview.domain.interview.domain.entity.InterviewEntity;
import bumaview.bumaview.domain.interview.domain.repository.InterviewRepository;
import bumaview.bumaview.domain.interview.presentation.dto.req.InterviewAnswerRequestDto;
import bumaview.bumaview.domain.interview.presentation.dto.res.InterviewAnswerResponseDto;
import bumaview.bumaview.domain.interview.presentation.dto.res.InterviewQuestionResponseDto;
import bumaview.bumaview.domain.interview.presentation.dto.res.MyAnswerListResponseDto;
import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import bumaview.bumaview.domain.question.domain.repository.QuestionRepository;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.domain.interview.exception.InterviewNotFoundException;
import bumaview.bumaview.domain.interview.exception.PortfolioRequiredException;
import bumaview.bumaview.domain.user.service.PdfToMarkdownConverter;
import bumaview.bumaview.global.exception.BumaviewException;
import bumaview.bumaview.global.security.user.BumaviewUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final PdfToMarkdownConverter pdfConverter;

    public InterviewQuestionResponseDto getRandomQuestion(BumaviewUserDetails bumaviewUserDetails) {
        UserEntity user = bumaviewUserDetails.userEntity();

        if (user.getPortfolio() == null || user.getPortfolio().isEmpty()) {
            throw new PortfolioRequiredException("포트폴리오를 먼저 등록해주세요.");
        }

        List<QuestionEntity> questions = questionRepository.findAll();
        if (questions.isEmpty()) {
            throw new InterviewNotFoundException("질문이 존재하지 않습니다.");
        }

        Random random = new Random();
        QuestionEntity randomQuestion = questions.get(random.nextInt(questions.size()));

        return InterviewQuestionResponseDto.from(randomQuestion);
    }

    @Transactional
    public InterviewAnswerResponseDto submitAnswer(BumaviewUserDetails bumaviewUserDetails, InterviewAnswerRequestDto request) {
        UserEntity user = bumaviewUserDetails.userEntity();

        if (user.getPortfolio() == null || user.getPortfolio().isEmpty()) {
            throw new PortfolioRequiredException("포트폴리오를 먼저 등록해주세요.");
        }

        QuestionEntity question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new InterviewNotFoundException("질문을 찾을 수 없습니다."));

        InterviewEntity interview = interviewRepository.findByUserAndQuestion(user, question)
                .orElseGet(() -> InterviewEntity.builder()
                        .user(user)
                        .question(question)
                        .answer(request.answer())
                        .build());

        if (interview.getId() != null) {
            interview.updateAnswer(request.answer());
        }

        interviewRepository.save(interview);

        String bestAnswer = "최고의 답변";

        return InterviewAnswerResponseDto.builder()
                .bestAnswer(bestAnswer)
                .build();
    }

    public List<MyAnswerListResponseDto> getMyAnswerList(BumaviewUserDetails bumaviewUserDetails) {
        UserEntity user = bumaviewUserDetails.userEntity();

        if (user.getPortfolio() == null || user.getPortfolio().isEmpty()) {
            throw new PortfolioRequiredException("포트폴리오를 먼저 등록해주세요.");
        }

        return interviewRepository.findByUser(user)
                .stream()
                .map(MyAnswerListResponseDto::from)
                .toList();
    }

    @Transactional
    public String uploadPortfolio(BumaviewUserDetails bumaviewUserDetails, MultipartFile file) {
        UserEntity user = bumaviewUserDetails.userEntity();

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new BumaviewException(HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
        }

        // Check if PDF file
        if (!pdfConverter.isPdfFile(file)) {
            throw new BumaviewException(HttpStatus.BAD_REQUEST, "해당 파일의 형식이 올바르지 않습니다.");
        }

        // Convert PDF to Markdown
        String markdownContent = pdfConverter.convertPdfToMarkdown(file);

        // Get managed entity and save
        UserEntity managedUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new InterviewNotFoundException("사용자를 찾을 수 없습니다."));

        managedUser.updateProfile(null, null, null, null, markdownContent, null);

        return file.getOriginalFilename();
    }

}