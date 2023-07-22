package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.RecentRecommendation;

import java.util.Optional;

public interface RecentRecommendationRepository extends JpaRepository<RecentRecommendation, Long> {
    Optional<RecentRecommendation> findByUserId(Long userId);
}
