package yeolJyeongKong.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.RestPage;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;

    @Cacheable(value = "Search", key = "#productName + '_' + #gender")
    public RestPage<ProductPreviewDto> products(String productName, String gender, Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.searchProduct(productName, gender, filterId, pageable));
    }
}
