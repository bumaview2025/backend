package bumaview.bumaview.domain.question.presentation.dto.res;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record QuestionResponseDto(
    Long id,
    String question,
    String category,
    String company,
    @JsonProperty("question_at")
    Integer questionAt
) {
    public static QuestionResponseDto from(QuestionEntity entity) {
        return QuestionResponseDto.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .category(entity.getCategory())
                .company(entity.getCompany())
                .questionAt(entity.getQuestionAt())
                .build();
    }
}