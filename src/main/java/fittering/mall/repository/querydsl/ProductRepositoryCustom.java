package fittering.mall.repository.querydsl;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.service.dto.BottomProductDto;
import fittering.mall.service.dto.DressProductDto;
import fittering.mall.service.dto.OuterProductDto;
import fittering.mall.service.dto.TopProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    List<Integer> findPopularAgeRangePercents(Long productId);
    List<Integer> findPopularGenderPercents(Long productId);
    List<ResponseProductPreviewDto> timeRank(String gender);
    Optional<LocalDateTime> maxUpdatedAt();
}
