package fittering.mall.repository.querydsl;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.entity.Favorite;

import java.util.List;

public interface FavoriteRepositoryCustom {
    List<Favorite> userFavoriteMall(Long userId);
    Page<ResponseProductPreviewDto> userFavoriteProduct(Long userId, Pageable pageable);
    List<ResponseProductPreviewDto> userFavoriteProductPreview(Long userId);
    void deleteByUserIdAndMallId(Long userId, Long mallId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    Boolean isUserFavoriteMall(Long userId, Long mallId);
    Boolean isUserFavoriteProduct(Long userId, Long productId);
}
