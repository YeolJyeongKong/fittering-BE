package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
