package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Recent;
import fittering.mall.repository.querydsl.RecentRepositoryCustom;


public interface RecentRepository extends JpaRepository<Recent, Long>, RecentRepositoryCustom {
}
