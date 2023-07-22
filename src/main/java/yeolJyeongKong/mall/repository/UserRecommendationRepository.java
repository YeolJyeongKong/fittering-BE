package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.UserRecommendation;

import java.util.Optional;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    Optional<UserRecommendation> findByUserId(Long userId);
}
