package fittering.mall.domain.mapper;

import fittering.mall.config.kafka.domain.dto.CrawledSizeDto;
import fittering.mall.domain.dto.controller.request.RequestBottomDto;
import fittering.mall.domain.dto.controller.request.RequestDressDto;
import fittering.mall.domain.dto.controller.request.RequestOuterDto;
import fittering.mall.domain.dto.controller.request.RequestTopDto;
import fittering.mall.domain.dto.controller.response.*;
import fittering.mall.domain.dto.service.*;
import fittering.mall.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SizeMapper {
    SizeMapper INSTANCE = Mappers.getMapper(SizeMapper.class);

    Bottom toBottom(BottomDto bottomDto);
    @Mappings({
            @Mapping(source = "bottomDto.bottom_width", target = "bottomWidth"),
            @Mapping(source = "bottomDto.hip_width", target = "hipWidth")
    })
    Bottom toBottom(CrawledSizeDto bottomDto);
    BottomDto toBottomDto(RequestBottomDto requestBottomDto);
    @Mappings({
        @Mapping(source = "favoriteCount", target = "favoriteCount"),
        @Mapping(source = "product.image", target = "productImage"),
        @Mapping(source = "product.name", target = "productName"),
        @Mapping(source = "product.gender", target = "productGender"),
        @Mapping(source = "product.price", target = "price"),
        @Mapping(source = "product.mall.name", target = "mallName"),
        @Mapping(source = "product.mall.url", target = "mallUrl"),
        @Mapping(source = "product.mall.image", target = "mallImage"),
        @Mapping(source = "product.origin", target = "origin"),
        @Mapping(source = "product.category.name", target = "category"),
        @Mapping(source = "product.view", target = "view"),
        @Mapping(source = "popularGender", target = "popularGender"),
        @Mapping(source = "popularAgeRange", target = "popularAgeRange"),
        @Mapping(source = "sizes", target = "sizes")
    })
    BottomProductDto toBottomProductDto(Long favoriteCount, Product product, String popularGender, Integer popularAgeRange, List<BottomDto> sizes);
    ResponseBottomDto toResponseBottomDto(BottomProductDto bottomProductDto);
    ResponseBottomSizeDto toResponseBottomSizeDto(BottomDto bottomDto);
    Dress toDress(DressDto dressDto);
    @Mappings({
            @Mapping(source = "dressDto.arm_hall", target = "armHall"),
            @Mapping(source = "dressDto.sleeve_width", target = "sleeveWidth"),
            @Mapping(source = "dressDto.bottom_width", target = "bottomWidth")
    })
    Dress toDress(CrawledSizeDto dressDto);
    DressDto toDressDto(RequestDressDto requestDressDto);
    @Mappings({
            @Mapping(source = "favoriteCount", target = "favoriteCount"),
            @Mapping(source = "product.image", target = "productImage"),
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.gender", target = "productGender"),
            @Mapping(source = "product.price", target = "price"),
            @Mapping(source = "product.mall.name", target = "mallName"),
            @Mapping(source = "product.mall.url", target = "mallUrl"),
            @Mapping(source = "product.mall.image", target = "mallImage"),
            @Mapping(source = "product.origin", target = "origin"),
            @Mapping(source = "product.category.name", target = "category"),
            @Mapping(source = "product.view", target = "view"),
            @Mapping(source = "popularGender", target = "popularGender"),
            @Mapping(source = "popularAgeRange", target = "popularAgeRange"),
            @Mapping(source = "sizes", target = "sizes")
    })
    DressProductDto toDressProductDto(Long favoriteCount, Product product, String popularGender, Integer popularAgeRange, List<DressDto> sizes);
    ResponseDressDto toResponseDressDto(DressProductDto dressProductDto);
    ResponseDressSizeDto toResponseDressSizeDto(DressDto dressDto);
    Top toTop(TopDto topDto);
    Top toTop(CrawledSizeDto topDto);
    TopDto toTopDto(RequestTopDto requestTopDto);
    @Mappings({
            @Mapping(source = "favoriteCount", target = "favoriteCount"),
            @Mapping(source = "product.image", target = "productImage"),
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.gender", target = "productGender"),
            @Mapping(source = "product.price", target = "price"),
            @Mapping(source = "product.mall.name", target = "mallName"),
            @Mapping(source = "product.mall.url", target = "mallUrl"),
            @Mapping(source = "product.mall.image", target = "mallImage"),
            @Mapping(source = "product.origin", target = "origin"),
            @Mapping(source = "product.category.name", target = "category"),
            @Mapping(source = "product.view", target = "view"),
            @Mapping(source = "popularGender", target = "popularGender"),
            @Mapping(source = "popularAgeRange", target = "popularAgeRange"),
            @Mapping(source = "sizes", target = "sizes")
    })
    TopProductDto toTopProductDto(Long favoriteCount, Product product, String popularGender, Integer popularAgeRange, List<TopDto> sizes);
    ResponseTopDto toResponseTopDto(TopProductDto topProductDto);
    ResponseTopSizeDto toResponseTopSizeDto(TopDto topDto);
    Outer toOuter(OuterDto outerDto);
    Outer toOuter(CrawledSizeDto outerDto);
    OuterDto toOuterDto(RequestOuterDto requestOuterDto);
    @Mappings({
            @Mapping(source = "favoriteCount", target = "favoriteCount"),
            @Mapping(source = "product.image", target = "productImage"),
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "product.gender", target = "productGender"),
            @Mapping(source = "product.price", target = "price"),
            @Mapping(source = "product.mall.name", target = "mallName"),
            @Mapping(source = "product.mall.url", target = "mallUrl"),
            @Mapping(source = "product.mall.image", target = "mallImage"),
            @Mapping(source = "product.origin", target = "origin"),
            @Mapping(source = "product.category.name", target = "category"),
            @Mapping(source = "product.view", target = "view"),
            @Mapping(source = "popularGender", target = "popularGender"),
            @Mapping(source = "popularAgeRange", target = "popularAgeRange"),
            @Mapping(source = "sizes", target = "sizes")
    })
    OuterProductDto toOuterProductDto(Long favoriteCount, Product product, String popularGender, Integer popularAgeRange, List<OuterDto> sizes);
    ResponseOuterDto toResponseOuterDto(OuterProductDto outerProductDto);
    ResponseOuterSizeDto toResponseOuterSizeDto(OuterDto outerDto);
}
