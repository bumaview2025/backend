package bumaview.bumaview.domain.interview.presentation.dto.res;

import lombok.Builder;

@Builder
public record InterviewAnswerResponseDto(
    String bestAnswer
) {
}