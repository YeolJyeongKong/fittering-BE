package fittering.mall.repository.querydsl;

import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.dto.service.BottomProductDto;
import fittering.mall.domain.dto.service.DressProductDto;
import fittering.mall.domain.dto.service.OuterProductDto;
import fittering.mall.domain.dto.service.TopProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    ResponseProductPreviewDto productById(Long productId);
    Page<ResponseProductPreviewDto> productWithCategory(Long mallId, Long categoryId, String gender, Long filterId, Pageable pageable);
    Page<ResponseProductPreviewDto> productWithSubCategory(Long mallId, Long subCategoryId, String gender, Long filterId, Pageable pageable);
    Page<ResponseProductPreviewDto> searchProduct(String productName, String gender, Long filterId, Pageable pageable);
    List<String> relatedSearch(String keyword);
    Long productCountWithCategory(Long categoryId);
    Long productCountWithSubCategory(Long categoryId);
    Long productCountWithCategoryOfMall(String mallName, Long categoryId);
    Long productCountWithSubCategoryOfMall(String mallName, Long subCategoryId);
    OuterProductDto outerProductDetail(Long productId);
    TopProductDto topProductDetail(Long productId);
    DressProductDto dressProductDetail(Long productId);
    BottomProductDto bottomProductDetail(Long productId);
    Long findFavoriteCount(Long favoriteId);
    Long findRecentCount(Long recentId);
    Long findRecentRecommendation(Long recentRecommendationId);
    Long findUserRecommendation(Long userRecommendationId);
    List<ResponseProductPreviewDto> timeRank();
}
