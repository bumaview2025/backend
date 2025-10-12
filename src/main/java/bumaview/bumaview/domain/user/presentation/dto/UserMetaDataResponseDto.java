package bumaview.bumaview.domain.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserMetaDataResponseDto {

    @JsonProperty("metadata")
    private String metadata;

    @JsonProperty("github_data")
    private List<Object> githubData;
}