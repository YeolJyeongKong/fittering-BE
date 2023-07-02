package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yeolJyeongKong.mall.domain.entity.Mall;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public FavoriteRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Mall> userFavoriteMall(Long userId) {
        return queryFactory
                .select(mall)
                .from(user)
                .leftJoin(user.malls, mall)
                .where(
                        userIdEq(userId)
                )
                .fetch();
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
