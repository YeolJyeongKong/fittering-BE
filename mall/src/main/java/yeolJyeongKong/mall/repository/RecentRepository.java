package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Recent;
import yeolJyeongKong.mall.repository.querydsl.RecentRepositoryCustom;

import java.util.Optional;

public interface RecentRepository extends JpaRepository<Recent, Long>, RecentRepositoryCustom {
    Optional<Recent> findByUserId(Long userId);
}
