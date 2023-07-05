package yeolJyeongKong.mall.repository.querydsl;

import yeolJyeongKong.mall.domain.entity.Favorite;

import java.util.List;

public interface FavoriteRepositoryCustom {
    List<Favorite> userFavoriteMall(Long userId);
}
