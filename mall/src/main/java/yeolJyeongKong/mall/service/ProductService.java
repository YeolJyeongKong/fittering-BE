package yeolJyeongKong.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductPreviewDto> productWithCategory(String categoryName, String gender, Pageable pageable) {
        return productRepository.productWithCategory(categoryName, gender, pageable);
    }

    public Page<ProductPreviewDto> productWithUserFavorite(Long userId, Pageable pageable) {
        return productRepository.productWithFavorite(userId, pageable);
    }
}
