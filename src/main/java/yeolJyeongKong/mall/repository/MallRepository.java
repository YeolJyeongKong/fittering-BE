package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.repository.querydsl.MallRepositoryCustom;

import java.util.Optional;

public interface MallRepository extends JpaRepository<Mall, Long>, MallRepositoryCustom {
    Optional<Mall> findByName(String name);
}
