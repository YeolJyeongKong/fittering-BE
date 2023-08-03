package fittering.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Product;
import fittering.mall.repository.querydsl.ProductRepositoryCustom;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    @Override
    @EntityGraph(attributePaths = {"mall", "category"})
    Optional<Product> findById(Long productId);
}
