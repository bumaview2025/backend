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
import bumaview.bumaview.global.security.user.BumaviewUserDetails;
import bumaview.bumaview.global.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

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
        
        try {
            String fileName = fileService.uploadPortfolioFile(file, user.getUserId().toString());
            
            UserEntity managedUser = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new InterviewNotFoundException("사용자를 찾을 수 없습니다."));
            
            managedUser.updateProfile(null, null, null, null, fileName, null);
            
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

}