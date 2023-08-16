package fittering.mall.repository.querydsl;

import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;

import java.util.List;

public interface MallRepositoryCustom {
    List<ResponseProductPreviewDto> findProducts(String mallName);
    Long findFavoriteCount(Long mallId);
    List<String> relatedSearch(String keyword);
}
