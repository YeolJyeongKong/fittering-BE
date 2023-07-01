package yeolJyeongKong.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;

public interface ProductRepositoryCustom {
    Page<ProductPreviewDto> productWithCategory(String category, String gender, Pageable pageable);
    Page<ProductPreviewDto> productWithFavorite(Long userId, Pageable pageable);
    Page<ProductPreviewDto> searchProduct(String productName, String gender, Pageable pageable);
    Long productCountWithCategory(String category);
    Long productCountWithCategoryOfMall(String mallName, String category);
}
