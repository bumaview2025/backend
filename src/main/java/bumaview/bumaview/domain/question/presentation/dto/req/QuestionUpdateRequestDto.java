package bumaview.bumaview.domain.question.presentation.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record QuestionUpdateRequestDto(
    Long id,
    String question,
    String category,
    String company,
    @JsonProperty("question_at")
    @JsonAlias("questionAt")
    Integer questionAt
) {
}