package fittering.mall.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Product;
import fittering.mall.repository.querydsl.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    @Override
    @EntityGraph(attributePaths = {"mall", "category"})
    Optional<Product> findById(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findByName(String name);
}
