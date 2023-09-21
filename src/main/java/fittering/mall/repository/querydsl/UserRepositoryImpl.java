package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.repository.dto.QSavedMeasurementDto;
import fittering.mall.repository.dto.QSavedUserDto;
import fittering.mall.repository.dto.SavedMeasurementDto;
import fittering.mall.repository.dto.SavedUserDto;
import fittering.mall.service.dto.MeasurementDto;
import fittering.mall.service.dto.UserDto;
import fittering.mall.domain.mapper.MeasurementMapper;
import fittering.mall.domain.mapper.UserMapper;
import jakarta.persistence.EntityManager;

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
        SavedUserDto savedUserDto = queryFactory
                .select(new QSavedUserDto(
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
        return UserMapper.INSTANCE.toUserDto(savedUserDto);
    }

    @Override
    public MeasurementDto measurementInfo(Long userId) {
        SavedMeasurementDto savedMeasurementDto = queryFactory
                .select(new QSavedMeasurementDto(
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
        return MeasurementMapper.INSTANCE.toMeasurementDto(savedMeasurementDto);
    }

    @Override
    public String findGenderById(Long userId) {
        return queryFactory
                .select(user.gender)
                .from(user)
                .where(
                        userIdEq(userId)
                )
                .fetchOne();
    }
}
