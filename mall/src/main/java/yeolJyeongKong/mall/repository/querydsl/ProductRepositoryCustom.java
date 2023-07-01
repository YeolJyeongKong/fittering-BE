package yeolJyeongKong.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;

public interface ProductRepositoryCustom {
    ProductPreviewDto productById(Long productId);
    Page<ProductPreviewDto> productWithCategory(Long mallId, Long categoryId, String gender, Pageable pageable);
    Page<ProductPreviewDto> productWithFavorite(Long userId, Pageable pageable);
    Page<ProductPreviewDto> searchProduct(String productName, String gender, Pageable pageable);
    Long productCountWithCategory(Long categoryId);
    Long productCountWithCategoryOfMall(String mallName, Long categoryId);
}
