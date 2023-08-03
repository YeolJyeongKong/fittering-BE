package fittering.mall.repository.querydsl;

import fittering.mall.domain.dto.MeasurementDto;
import fittering.mall.domain.dto.UserDto;

public interface UserRepositoryCustom {
    UserDto info(Long userId);
    MeasurementDto measurementInfo(Long userId);
}
