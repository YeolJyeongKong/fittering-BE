package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Mall;

import java.util.Optional;

public interface MallRepository extends JpaRepository<Mall, Long> {
    Optional<Mall> findByName(String name);
}
