package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.ProductDescription;

public interface ProductDescriptionRepository extends JpaRepository<ProductDescription, Long> {
}
