package yeolJyeongKong.mall.repository.querydsl;

import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;

import java.util.List;

public interface RecentRepositoryCustom {
    List<ProductPreviewDto> recentProduct(Long userId);
}
