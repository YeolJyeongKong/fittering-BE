package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static fittering.mall.domain.entity.QRecentRecommendation.recentRecommendation;
import static fittering.mall.repository.querydsl.EqualMethod.userIdEq;

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
