package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Rank;
import fittering.mall.repository.querydsl.RankRepositoryCustom;

import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long>, RankRepositoryCustom {
    Optional<Rank> findByUserIdAndMallId(Long userId, Long mallId);
    void deleteByUserId(Long userId);
}