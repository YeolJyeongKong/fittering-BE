package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.UserRecommendation;

import java.util.List;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<UserRecommendation> findByUserId(Long userId);
}
