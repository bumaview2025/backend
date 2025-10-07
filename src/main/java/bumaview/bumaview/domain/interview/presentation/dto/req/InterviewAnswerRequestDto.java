package bumaview.bumaview.domain.interview.presentation.dto.req;

import lombok.Builder;

@Builder
public record InterviewAnswerRequestDto(
    Long questionId,
    String answer
) {
}