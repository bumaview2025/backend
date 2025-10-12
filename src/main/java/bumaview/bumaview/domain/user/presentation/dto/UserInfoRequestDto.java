package bumaview.bumaview.domain.user.presentation.dto;

import bumaview.bumaview.domain.user.domain.entity.DreamJob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequestDto {

    @NotBlank(message = "사용자 이름은 필수입니다")
    private String username;

    @NotNull(message = "희망 직무는 필수입니다")
    private DreamJob dreamJob;

    private MultipartFile portfolio;

    @NotBlank(message = "GitHub 저장소는 필수입니다")
    private String githubRepository;
}