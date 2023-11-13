package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static fittering.mall.domain.entity.QUserRecommendation.userRecommendation;
import static fittering.mall.repository.querydsl.EqualMethod.userIdEq;

public class UserRecommendationRepositoryImpl implements UserRecommendationRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public UserRecommendationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory
                .delete(userRecommendation)
                .where(
                        userIdEq(userId)
                )
                .execute();
    }
}
