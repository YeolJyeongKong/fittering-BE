package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static yeolJyeongKong.mall.domain.entity.QRecentRecommendation.recentRecommendation;
import static yeolJyeongKong.mall.repository.querydsl.EqualMethod.userIdEq;

public class RecentRecommendationRepositoryImpl implements RecentRecommendationRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public RecentRecommendationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory
                .delete(recentRecommendation)
                .where(
                        userIdEq(userId)
                )
                .execute();
    }
}
