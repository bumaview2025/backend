package bumaview.bumaview.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "github_profile")
    private String githubProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "dream_job")
    private DreamJob dreamJob;

    @Column(name = "portfolio", columnDefinition = "json")
    private String portfolio;

    @Column(name = "gender")
    private String gender;

    @Builder(buildMethodName = "signupBuilder")
    public UserEntity(String username) {
        this.username = username;
    }

    @Builder
    public UserEntity(String username, String password,
                     String githubProfile, DreamJob dreamJob, String portfolio, String gender) {
        this.username = username;
        this.password = password;
        this.githubProfile = githubProfile;
        this.dreamJob = dreamJob;
        this.portfolio = portfolio;
        this.gender = gender;
    }
}
