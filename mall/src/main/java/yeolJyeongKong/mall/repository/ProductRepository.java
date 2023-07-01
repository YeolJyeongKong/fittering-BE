package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.repository.querydsl.ProductRepositoryCustom;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
