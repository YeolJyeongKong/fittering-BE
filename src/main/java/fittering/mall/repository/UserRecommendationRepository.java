package fittering.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.UserRecommendation;
import fittering.mall.repository.querydsl.UserRecommendationRepositoryCustom;

import java.util.List;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long>, UserRecommendationRepositoryCustom {
    @EntityGraph(attributePaths = {"product"})
    List<UserRecommendation> findByUserId(Long userId);
}
