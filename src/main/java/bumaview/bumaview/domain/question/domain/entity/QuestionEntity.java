package bumaview.bumaview.domain.question.domain.entity;

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
@Table(name = "tbl_question", indexes = {
    @Index(name = "idx_question_category", columnList = "category"),
    @Index(name = "idx_question_company", columnList = "company"),
    @Index(name = "idx_question_year", columnList = "question_at")
})
@EntityListeners(AuditingEntityListener.class)
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "company", nullable = false, length = 100)
    private String company;

    @Column(name = "question_at", nullable = false)
    private Integer questionAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public QuestionEntity(String question, String category, String company, Integer questionAt) {
        this.question = question;
        this.category = category;
        this.company = company;
        this.questionAt = questionAt != null ? questionAt : LocalDateTime.now().getYear();
        this.isActive = true;
    }

    public void updateQuestion(String question, String category, String company, Integer questionAt) {
        if (question != null) this.question = question;
        if (category != null) this.category = category;
        if (company != null) this.company = company;
        if (questionAt != null) this.questionAt = questionAt;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}