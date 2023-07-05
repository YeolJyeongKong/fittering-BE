package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yeolJyeongKong.mall.domain.entity.Favorite;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QFavorite.favorite;
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public FavoriteRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Favorite> userFavoriteMall(Long userId) {
        return queryFactory
                .select(favorite)
                .from(user)
                .leftJoin(user.favorites, favorite)
                .leftJoin(favorite.mall, mall)
                .where(
                        userIdEq(userId)
                )
                .fetch();
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
