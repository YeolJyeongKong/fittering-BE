package fittering.mall.domain.mapper;

import fittering.mall.domain.dto.controller.request.RequestProductDetailDto;
import fittering.mall.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "requestProductDetailDto.name", target = "name"),
            @Mapping(source = "requestProductDetailDto.image", target = "image"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "subCategory", target = "subCategory"),
            @Mapping(source = "mall", target = "mall")
    })
    Product toProduct(RequestProductDetailDto requestProductDetailDto, Integer view, Integer timeView,
                      Category category, SubCategory subCategory, Mall mall);
    @Mapping(source = "descriptionImage", target = "url")
    DescriptionImage toDescriptionImage(String descriptionImage);
}
