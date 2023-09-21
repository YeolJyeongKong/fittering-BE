package fittering.mall.domain.mapper;

import fittering.mall.controller.dto.response.ResponseProductCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    ResponseProductCategoryDto toResponseProductCategoryDto(String category, Long count);
}
