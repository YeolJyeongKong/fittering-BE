package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Rank;
import yeolJyeongKong.mall.repository.querydsl.RankRepositoryCustom;

public interface RankRepository extends JpaRepository<Rank, Long>, RankRepositoryCustom {
}