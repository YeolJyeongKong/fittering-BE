package fittering.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.RecentRecommendation;
import fittering.mall.repository.querydsl.RecentRecommendationRepositoryCustom;

import java.util.List;

public interface RecentRecommendationRepository extends JpaRepository<RecentRecommendation, Long>, RecentRecommendationRepositoryCustom {
    @EntityGraph(attributePaths = {"product"})
    List<RecentRecommendation> findByUserId(Long userId);
}
