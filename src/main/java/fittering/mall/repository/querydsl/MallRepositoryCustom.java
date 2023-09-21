package fittering.mall.repository.querydsl;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.service.dto.MallNameAndIdDto;

import java.util.List;

public interface MallRepositoryCustom {
    List<ResponseProductPreviewDto> findProducts(String mallName);
    Long findFavoriteCount(Long mallId);
    List<String> relatedSearch(String keyword);
    List<MallNameAndIdDto> findMallNameAndIdList();
}
