package fittering.mall.service;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.repository.MallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.RestPage;
import fittering.mall.repository.ProductRepository;

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
    public List<String> relatedSearchProducts(String keyword) {
        return productRepository.relatedSearch(keyword);
    }

    @Cacheable(value = "RelatedSearch", key = "'mall_' + #keyword")
    public List<String> relatedSearchMalls(String keyword) {
        return mallRepository.relatedSearch(keyword);
    }
}
