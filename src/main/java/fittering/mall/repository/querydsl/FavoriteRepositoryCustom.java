package fittering.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.dto.ProductPreviewDto;
import fittering.mall.domain.entity.Favorite;

import java.util.List;

public interface FavoriteRepositoryCustom {
    List<Favorite> userFavoriteMall(Long userId);
    Page<ProductPreviewDto> userFavoriteProduct(Long userId, Pageable pageable);
    void deleteByUserIdAndMallId(Long userId, Long mallId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
