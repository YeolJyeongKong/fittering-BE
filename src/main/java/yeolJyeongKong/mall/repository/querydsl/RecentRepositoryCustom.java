package yeolJyeongKong.mall.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.Recent;

import java.util.List;

public interface RecentRepositoryCustom {
    List<Recent> findByUserId(Long userId);
    List<ProductPreviewDto> recentProductPreview(Long userId);
    Page<ProductPreviewDto> recentProduct(Long userId, Pageable pageable);
    void initializeRecents(Long userId);
}
