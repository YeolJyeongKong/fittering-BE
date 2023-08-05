package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.domain.dto.repository.QSavedMeasurementDto;
import fittering.mall.domain.dto.repository.QSavedUserDto;
import fittering.mall.domain.dto.repository.SavedMeasurementDto;
import fittering.mall.domain.dto.repository.SavedUserDto;
import fittering.mall.domain.dto.service.MeasurementDto;
import fittering.mall.domain.dto.service.UserDto;
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
}
