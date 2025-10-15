package bumaview.bumaview.domain.interview.presentation.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record InterviewAnswerRequestDto(
    @JsonProperty("questionId")
    @JsonAlias("question_id")
    Long questionId,
    String answer
) {
}