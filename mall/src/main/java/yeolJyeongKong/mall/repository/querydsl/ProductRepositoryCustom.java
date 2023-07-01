package yeolJyeongKong.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<ProductPreviewDto> productCategoryPreview(String categoryName, String gender, Pageable pageable);
    Page<ProductPreviewDto> productLikePreview(Long userId, Pageable pageable);
    Page<ProductPreviewDto> productSearchPreview(String productName, String gender, Pageable pageable);
}
