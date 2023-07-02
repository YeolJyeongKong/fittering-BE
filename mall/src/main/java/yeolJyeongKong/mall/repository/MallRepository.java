package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Mall;

public interface MallRepository extends JpaRepository<Mall, Long> {
}
