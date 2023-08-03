package fittering.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.dto.*;
import fittering.mall.domain.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    ProductPreviewDto productById(Long productId);
    Page<ProductPreviewDto> productWithCategory(Long mallId, Long categoryId, String gender, Long filterId, Pageable pageable);
    Page<ProductPreviewDto> productWithSubCategory(Long mallId, Long subCategoryId, String gender, Long filterId, Pageable pageable);
    Page<ProductPreviewDto> searchProduct(String productName, String gender, Long filterId, Pageable pageable);
    Long productCountWithCategory(Long categoryId);
    Long productCountWithSubCategory(Long categoryId);
    Long productCountWithCategoryOfMall(String mallName, Long categoryId);
    Long productCountWithSubCategoryOfMall(String mallName, Long subCategoryId);
    OuterProductDto outerProductDetail(Long productId);
    TopProductDto topProductDetail(Long productId);
    DressProductDto dressProductDetail(Long productId);
    BottomProductDto bottomProductDetail(Long productId);
    List<Product> findByIds(List<Long> productIds);
    Long findFavoriteCount(Long favoriteId);
    Long findRecentCount(Long recentId);
    Long findRecentRecommendation(Long recentRecommendationId);
    Long findUserRecommendation(Long userRecommendationId);
}