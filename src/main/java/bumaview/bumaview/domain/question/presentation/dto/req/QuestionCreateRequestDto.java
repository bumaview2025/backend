package bumaview.bumaview.domain.question.presentation.dto.req;

import lombok.Builder;

@Builder
public record QuestionCreateRequestDto(
    String question,
    String category,
    String company,
    Integer questionAt
) {
}