package fittering.mall.domain.mapper;

import fittering.mall.config.kafka.domain.dto.CrawledProductDto;
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
            @Mapping(source = "requestProductDetailDto.origin", target = "origin"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "subCategory", target = "subCategory"),
            @Mapping(source = "mall", target = "mall"),
            @Mapping(source = "disabled", target = "disabled")
    })
    Product toProduct(RequestProductDetailDto requestProductDetailDto, Integer view, Integer timeView,
                      Category category, SubCategory subCategory, Mall mall, Integer disabled);
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "crawledProductDto.name", target = "name"),
            @Mapping(source = "crawledProductDto.url", target = "origin"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "subCategory", target = "subCategory"),
            @Mapping(source = "mall", target = "mall"),
    })
    Product toProduct(CrawledProductDto crawledProductDto, String image, Integer view,
                      Integer timeView, Category category, SubCategory subCategory, Mall mall);
    @Mapping(source = "productDescription", target = "url")
    ProductDescription toProductDescription(String productDescription);
}
