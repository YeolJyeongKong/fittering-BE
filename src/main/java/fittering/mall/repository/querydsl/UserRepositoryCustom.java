package fittering.mall.repository.querydsl;

import fittering.mall.service.dto.MeasurementDto;
import fittering.mall.service.dto.UserDto;

public interface UserRepositoryCustom {
    UserDto info(Long userId);
    MeasurementDto measurementInfo(Long userId);
    String findGenderById(Long userId);
}
