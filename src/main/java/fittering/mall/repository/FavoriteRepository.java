package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Favorite;
import fittering.mall.repository.querydsl.FavoriteRepositoryCustom;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>, FavoriteRepositoryCustom {
    void deleteByUserId(Long userId);
}
