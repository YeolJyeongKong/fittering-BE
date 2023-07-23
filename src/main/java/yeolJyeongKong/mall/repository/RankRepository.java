package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Rank;
import yeolJyeongKong.mall.repository.querydsl.RankRepositoryCustom;

import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long>, RankRepositoryCustom {
    Optional<Rank> findByUserIdAndMallId(Long userId, Long mallId);
    void deleteByUserId(Long userId);
}