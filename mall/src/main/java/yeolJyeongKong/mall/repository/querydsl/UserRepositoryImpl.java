package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yeolJyeongKong.mall.domain.dto.*;

import static yeolJyeongKong.mall.domain.entity.QMeasurement.measurement;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class UserRepositoryImpl implements UserRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public UserDto info(Long userId) {
        return queryFactory
                .select(new QUserDto(
                        user.email,
                        user.username,
                        user.gender,
                        user.year,
                        user.month,
                        user.day
                ))
                .from(user)
                .where(
                        userIdEq(userId)
                )
                .fetchOne();
    }

    @Override
    public MeasurementDto measurementInfo(Long userId) {
        return queryFactory
                .select(new QMeasurementDto(
                        measurement.height,
                        measurement.weight,
                        measurement.arm,
                        measurement.leg,
                        measurement.shoulder,
                        measurement.waist,
                        measurement.chest,
                        measurement.thigh,
                        measurement.hip
                ))
                .from(user)
                .leftJoin(user.measurement, measurement)
                .where(
                        userIdEq(userId)
                )
                .fetchOne();
    }

    @Override
    public Long usernameCount(String username) {
        return queryFactory
                .select(user.count())
                .from(user)
                .where(user.username.eq(username))
                .fetchOne();
    }

    @Override
    public Long emailCount(String email) {
        return queryFactory
                .select(user.count())
                .from(user)
                .where(user.email.eq(email))
                .fetchOne();
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
