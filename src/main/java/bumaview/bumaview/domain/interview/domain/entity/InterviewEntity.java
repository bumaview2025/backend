package bumaview.bumaview.domain.interview.domain.entity;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_interview", 
       indexes = {
           @Index(name = "idx_interview_user", columnList = "user_id"),
           @Index(name = "idx_interview_question", columnList = "question_id"),
           @Index(name = "idx_interview_created", columnList = "created_at")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_question", columnNames = {"user_id", "question_id"})
       })
@EntityListeners(AuditingEntityListener.class)
public class InterviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_interview_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, foreignKey = @ForeignKey(name = "fk_interview_question"))
    private QuestionEntity question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "score")
    private Integer score;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Builder
    public InterviewEntity(UserEntity user, QuestionEntity question, String answer) {
        this.user = user;
        this.question = question;
        this.answer = answer;
    }

    public void updateScore(Integer score) {
        this.score = score;
    }

    public void updateFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void updateAnswer(String answer) {
        if (answer != null) this.answer = answer;
    }

    public void updateScoreAndFeedback(Integer score, String feedback) {
        if (score != null) this.score = score;
        if (feedback != null) this.feedback = feedback;
    }
}