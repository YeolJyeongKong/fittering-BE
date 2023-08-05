package fittering.mall.repository.querydsl;

import fittering.mall.domain.dto.service.MeasurementDto;
import fittering.mall.domain.dto.service.UserDto;

public interface UserRepositoryCustom {
    UserDto info(Long userId);
    MeasurementDto measurementInfo(Long userId);
}
