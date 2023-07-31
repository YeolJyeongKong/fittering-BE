package yeolJyeongKong.mall.repository.querydsl;

import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.Recent;

import java.util.List;

public interface RecentRepositoryCustom {
    List<Recent> findByUserId(Long userId);
    List<ProductPreviewDto> recentProduct(Long userId);
}
