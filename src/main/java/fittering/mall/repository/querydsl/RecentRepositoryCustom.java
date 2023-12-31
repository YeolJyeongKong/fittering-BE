package fittering.mall.repository.querydsl;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.entity.Recent;

import java.util.List;

public interface RecentRepositoryCustom {
    List<Recent> findByUserId(Long userId);
    List<ResponseProductPreviewDto> recentProductPreview(Long userId);
    Page<ResponseProductPreviewDto> recentProduct(Long userId, Pageable pageable);
    void initializeRecents(Long userId);
    List<Long> recentProductIds(Long userId);
    Boolean isRecentProduct(Long userId, Long productId);
}
