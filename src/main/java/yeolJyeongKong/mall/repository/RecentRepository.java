package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Recent;
import yeolJyeongKong.mall.repository.querydsl.RecentRepositoryCustom;


public interface RecentRepository extends JpaRepository<Recent, Long>, RecentRepositoryCustom {
}
