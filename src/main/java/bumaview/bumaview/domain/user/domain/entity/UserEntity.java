package bumaview.bumaview.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_user", 
       indexes = {
           @Index(name = "idx_user_username", columnList = "username"),
           @Index(name = "idx_user_sub", columnList = "sub"),
           @Index(name = "idx_user_dream_job", columnList = "dream_job")
       })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "github_profile", length = 255)
    private String githubProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "dream_job", length = 20)
    private DreamJob dreamJob;

    @Column(name = "portfolio", columnDefinition = "TEXT")
    private String portfolio;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "sub", unique = true, nullable = false, length = 255)
    private String sub;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder(buildMethodName = "signupBuilder")
    public UserEntity(String username, String sub, String email, String profileImageUrl) {
        this.username = username;
        this.sub = sub;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.isActive = true;
    }

    @Builder
    public UserEntity(String username, String password, String githubProfile, DreamJob dreamJob, 
                     String portfolio, String gender, String sub, String email, String profileImageUrl) {
        this.username = username;
        this.password = password;
        this.githubProfile = githubProfile;
        this.dreamJob = dreamJob;
        this.portfolio = portfolio;
        this.gender = gender;
        this.sub = sub;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.isActive = true;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void updateProfile(String username, String password, String githubProfile, 
                             DreamJob dreamJob, String portfolio, String gender) {
        if (username != null) this.username = username;
        if (password != null) this.password = password;
        if (githubProfile != null) this.githubProfile = githubProfile;
        if (dreamJob != null) this.dreamJob = dreamJob;
        if (portfolio != null) this.portfolio = portfolio;
        if (gender != null) this.gender = gender;
    }
}
