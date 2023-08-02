package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.UserRecommendation;
import yeolJyeongKong.mall.repository.querydsl.UserRecommendationRepositoryCustom;

import java.util.List;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long>, UserRecommendationRepositoryCustom {
    @EntityGraph(attributePaths = {"product"})
    List<UserRecommendation> findByUserId(Long userId);
}
