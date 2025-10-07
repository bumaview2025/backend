package bumaview.bumaview.domain.interview.domain.repository;

import bumaview.bumaview.domain.interview.domain.entity.InterviewEntity;
import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<InterviewEntity, Long> {
    List<InterviewEntity> findByUser(UserEntity user);
    Optional<InterviewEntity> findByUserAndQuestion(UserEntity user, QuestionEntity question);
}