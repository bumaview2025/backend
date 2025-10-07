package bumaview.bumaview.domain.question.domain.repository;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    @Query("SELECT q FROM QuestionEntity q WHERE " +
           "(:column = 'question' AND q.question LIKE %:query%) OR " +
           "(:column = 'category' AND q.category LIKE %:query%) OR " +
           "(:column = 'company' AND q.company LIKE %:query%) OR " +
           "(:column = 'question_at' AND CAST(q.questionAt AS string) LIKE %:query%)")
    List<QuestionEntity> findByColumnAndQuery(@Param("column") String column, @Param("query") String query);
    
    Optional<QuestionEntity> findById(Long id);
}