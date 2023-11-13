package fittering.mall.repository.querydsl;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.service.dto.MallNameAndIdDto;
import fittering.mall.service.dto.RelatedSearchDto;

import java.util.List;

public interface MallRepositoryCustom {
    List<ResponseProductPreviewDto> findProducts(String mallName);
    List<RelatedSearchDto> relatedSearch(String keyword);
    List<MallNameAndIdDto> findMallNameAndIdList();
}
