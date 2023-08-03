package fittering.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.dto.ProductPreviewDto;
import fittering.mall.domain.entity.Recent;

import java.util.List;

public interface RecentRepositoryCustom {
    List<Recent> findByUserId(Long userId);
    List<ProductPreviewDto> recentProductPreview(Long userId);
    Page<ProductPreviewDto> recentProduct(Long userId, Pageable pageable);
    void initializeRecents(Long userId);
}
