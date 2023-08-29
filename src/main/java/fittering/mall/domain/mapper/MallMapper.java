package fittering.mall.domain.mapper;

import fittering.mall.domain.dto.controller.request.RequestMallDto;
import fittering.mall.domain.dto.controller.request.RequestMallRankProductDto;
import fittering.mall.domain.dto.controller.response.ResponseMallDto;
import fittering.mall.domain.dto.controller.response.ResponseMallWithProductDto;
import fittering.mall.domain.dto.controller.response.ResponseMallPreviewDto;
import fittering.mall.domain.dto.controller.response.ResponseMallRankProductDto;
import fittering.mall.domain.dto.service.MallPreviewDto;
import fittering.mall.domain.dto.repository.SavedMallPreviewDto;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.dto.service.MallRankProductDto;
import fittering.mall.domain.entity.Mall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MallMapper {
    MallMapper INSTANCE = Mappers.getMapper(MallMapper.class);

    Mall toMall(MallDto mallDto);
    MallDto toMallDto(RequestMallDto requestMallDto);
    MallRankProductDto toMallRankProductDto(RequestMallRankProductDto requestMallRankProductDto);
    ResponseMallDto toResponseMallDto(Mall mall, Integer view);
    @Mapping(source = "products", target = "products")
    ResponseMallWithProductDto toResponseMallWithProductDto(Mall mall, List<ResponseMallRankProductDto> products, Integer view);
    MallPreviewDto toMallPreviewDto(SavedMallPreviewDto savedMallPreviewDto);
    ResponseMallPreviewDto toResponseMallPreviewDto(MallPreviewDto mallPreviewDto);
}
