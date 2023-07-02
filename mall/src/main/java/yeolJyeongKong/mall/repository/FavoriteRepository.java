package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Favorite;
import yeolJyeongKong.mall.repository.querydsl.FavoriteRepositoryCustom;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, FavoriteRepositoryCustom {
    void deleteByUserIdAndMallId(Long userId, Long mallId);
}
