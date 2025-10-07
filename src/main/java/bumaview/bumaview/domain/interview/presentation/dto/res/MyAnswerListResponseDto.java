package bumaview.bumaview.domain.interview.presentation.dto.res;

import bumaview.bumaview.domain.interview.domain.entity.InterviewEntity;
import lombok.Builder;

@Builder
public record MyAnswerListResponseDto(
    InterviewQuestionResponseDto question,
    String answer
) {
    public static MyAnswerListResponseDto from(InterviewEntity entity) {
        return MyAnswerListResponseDto.builder()
                .question(InterviewQuestionResponseDto.from(entity.getQuestion()))
                .answer(entity.getAnswer())
                .build();
    }
}