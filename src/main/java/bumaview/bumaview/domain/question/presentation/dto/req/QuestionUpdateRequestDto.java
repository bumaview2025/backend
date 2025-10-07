package bumaview.bumaview.domain.question.presentation.dto.req;

import lombok.Builder;

@Builder
public record QuestionUpdateRequestDto(
    Long id,
    String question,
    String category,
    String company,
    Integer questionAt
) {
}