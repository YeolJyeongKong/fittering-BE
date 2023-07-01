package yeolJyeongKong.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;

    public Page<ProductPreviewDto> products(String productName, String gender, Pageable pageable) {
        return productRepository.searchProduct(productName, gender, pageable);
    }
}
