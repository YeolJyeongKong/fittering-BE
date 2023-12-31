package fittering.mall.service;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.controller.dto.response.ResponseRelatedSearchMallDto;
import fittering.mall.controller.dto.response.ResponseRelatedSearchProductDto;
import fittering.mall.domain.mapper.MallMapper;
import fittering.mall.domain.mapper.ProductMapper;
import fittering.mall.repository.MallRepository;
import fittering.mall.service.dto.RelatedSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.RestPage;
import fittering.mall.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final MallRepository mallRepository;

    @Cacheable(value = "Search", key = "#productName + '_' + #gender + '_' + #filterId")
    public RestPage<ResponseProductPreviewDto> products(String productName, String gender, Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.searchProduct(productName, gender, filterId, pageable));
    }

    @Cacheable(value = "RelatedSearch", key = "'product_' + #keyword")
    public List<ResponseRelatedSearchProductDto> relatedSearchProducts(String keyword) {
        List<ResponseRelatedSearchProductDto> responseRelatedSearchProductDtos = new ArrayList<>();
        List<RelatedSearchDto> relatedSearchDtos = productRepository.relatedSearch(keyword);
        relatedSearchDtos.forEach(relatedSearchDto ->
            responseRelatedSearchProductDtos.add(ProductMapper.INSTANCE.toResponseRelatedSearchProductDto(relatedSearchDto))
        );
        return responseRelatedSearchProductDtos;
    }

    @Cacheable(value = "RelatedSearch", key = "'mall_' + #keyword")
    public List<ResponseRelatedSearchMallDto> relatedSearchMalls(String keyword) {
        List<ResponseRelatedSearchMallDto> responseRelatedSearchMallDtos = new ArrayList<>();
        List<RelatedSearchDto> relatedSearchDtos = mallRepository.relatedSearch(keyword);
        relatedSearchDtos.forEach(relatedSearchDto ->
                responseRelatedSearchMallDtos.add(MallMapper.INSTANCE.toResponseRelatedSearchMallDto(relatedSearchDto))
        );
        return responseRelatedSearchMallDtos;
    }
}
