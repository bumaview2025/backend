package bumaview.bumaview.domain.user.presentation.dto;

import bumaview.bumaview.domain.user.domain.entity.DreamJob;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponseDto {

    private Long userId;
    private String username;
    private DreamJob dreamJob;
    private String portfolio;
    private String githubRepository;
    private JsonNode userMetaData;
    private JsonNode githubInfo;
    private String message;
}