package fittering.mall.domain.mapper;

import fittering.mall.config.kafka.domain.dto.CrawledMallDto;
import fittering.mall.controller.dto.request.RequestMallDto;
import fittering.mall.controller.dto.request.RequestMallRankProductDto;
import fittering.mall.controller.dto.response.*;
import fittering.mall.service.dto.*;
import fittering.mall.repository.dto.SavedMallPreviewDto;
import fittering.mall.domain.entity.Mall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MallMapper {
    MallMapper INSTANCE = Mappers.getMapper(MallMapper.class);

    Mall toMall(MallDto mallDto);
    @Mapping(target = "mallDto.id", ignore = true)
    Mall toMall(CrawledMallDto mallDto);
    MallDto toMallDto(RequestMallDto requestMallDto);
    MallRankProductDto toMallRankProductDto(RequestMallRankProductDto requestMallRankProductDto);
    ResponseMallDto toResponseMallDto(Mall mall, Integer view, Boolean isFavorite);
    @Mapping(source = "products", target = "products")
    ResponseMallWithProductDto toResponseMallWithProductDto(Mall mall, List<ResponseMallRankProductDto> products, Integer view, Boolean isFavorite);
    MallPreviewDto toMallPreviewDto(SavedMallPreviewDto savedMallPreviewDto);
    ResponseMallPreviewDto toResponseMallPreviewDto(MallPreviewDto mallPreviewDto, Boolean isFavorite);
    ResponseMallNameAndIdDto toResponseMallNameAndIdDto(MallNameAndIdDto mallNameAndIdDto);
    ResponseRelatedSearchMallDto toResponseRelatedSearchMallDto(RelatedSearchDto relatedSearchDto);
}
