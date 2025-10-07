package bumaview.bumaview.domain.interview.presentation.dto.res;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import lombok.Builder;

@Builder
public record InterviewQuestionResponseDto(
    Long id,
    String question,
    String category
) {
    public static InterviewQuestionResponseDto from(QuestionEntity entity) {
        return InterviewQuestionResponseDto.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .category(entity.getCategory())
                .build();
    }
}