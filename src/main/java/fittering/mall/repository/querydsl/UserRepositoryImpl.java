package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import fittering.mall.domain.dto.*;

import static fittering.mall.domain.entity.QMeasurement.measurement;
import static fittering.mall.domain.entity.QUser.user;
import static fittering.mall.repository.querydsl.EqualMethod.userIdEq;

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
}
