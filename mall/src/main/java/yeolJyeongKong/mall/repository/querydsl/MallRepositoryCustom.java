package yeolJyeongKong.mall.repository.querydsl;

import yeolJyeongKong.mall.domain.entity.Mall;

import java.util.List;

public interface MallRepositoryCustom {
    List<Mall> userFavoriteMall(Long userId);
}
