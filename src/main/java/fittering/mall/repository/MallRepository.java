package fittering.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Mall;
import fittering.mall.repository.querydsl.MallRepositoryCustom;

import java.util.Optional;

public interface MallRepository extends JpaRepository<Mall, Long>, MallRepositoryCustom {
    Optional<Mall> findByName(String name);
}
