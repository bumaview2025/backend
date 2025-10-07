package bumaview.bumaview.domain.user.infra.repository;

import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySub(String sub);
    Optional<UserEntity> findByUsername(String username);
}