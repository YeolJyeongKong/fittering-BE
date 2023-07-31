package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.RecentRecommendation;

import java.util.List;

public interface RecentRecommendationRepository extends JpaRepository<RecentRecommendation, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<RecentRecommendation> findByUserId(Long userId);
}
