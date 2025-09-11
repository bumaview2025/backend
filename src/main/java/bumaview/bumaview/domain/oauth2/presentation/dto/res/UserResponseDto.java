package bumaview.bumaview.domain.oauth2.presentation.dto.res;

import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import lombok.Builder;

@Builder
public record UserResponseDto(
    String username,
    String githubProfile,
    String dreamJob,
    String portfolio,
    String gender
) {
    public static UserResponseDto from(UserEntity user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .githubProfile(user.getGithubProfile())
                .dreamJob(user.getDreamJob().getValue())
                .gender(user.getGender())
                .portfolio(user.getPortfolio())
                .build();
    }
}
