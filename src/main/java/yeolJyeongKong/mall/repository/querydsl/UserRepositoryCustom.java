package yeolJyeongKong.mall.repository.querydsl;

import yeolJyeongKong.mall.domain.dto.MeasurementDto;
import yeolJyeongKong.mall.domain.dto.UserDto;

public interface UserRepositoryCustom {
    UserDto info(Long userId);
    MeasurementDto measurementInfo(Long userId);
}
