package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.RecentRecommendation;
import yeolJyeongKong.mall.repository.querydsl.RecentRecommendationRepositoryCustom;

import java.util.List;

public interface RecentRecommendationRepository extends JpaRepository<RecentRecommendation, Long>, RecentRecommendationRepositoryCustom {
    @EntityGraph(attributePaths = {"product"})
    List<RecentRecommendation> findByUserId(Long userId);
}
