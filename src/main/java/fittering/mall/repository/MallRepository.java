package fittering.mall.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Mall;
import fittering.mall.repository.querydsl.MallRepositoryCustom;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface MallRepository extends JpaRepository<Mall, Long>, MallRepositoryCustom {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "favorites")
    Optional<Mall> findByName(String name);
}
