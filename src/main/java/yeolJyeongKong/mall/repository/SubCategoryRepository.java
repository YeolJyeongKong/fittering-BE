package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.SubCategory;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findByName(String name);
}
