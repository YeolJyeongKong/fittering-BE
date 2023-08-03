package fittering.mall.repository.querydsl;

import fittering.mall.domain.dto.ProductPreviewDto;

import java.util.List;

public interface MallRepositoryCustom {
    List<ProductPreviewDto> findProducts(String mallName);
    Long findFavoriteCount(Long mallId);
}
