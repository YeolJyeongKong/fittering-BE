package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import yeolJyeongKong.mall.domain.dto.MeasurementDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.UserDto;
import yeolJyeongKong.mall.domain.entity.Measurement;
import yeolJyeongKong.mall.domain.entity.User;
import yeolJyeongKong.mall.repository.MeasurementRepository;
import yeolJyeongKong.mall.repository.RecentRepository;
import yeolJyeongKong.mall.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserService {

    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;
    private final RecentRepository recentRepository;

    public UserDto info(Long userId) {
        return userRepository.info(userId);
    }

    @Transactional
    public void infoUpdate(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user dosen't exist"));
        user.update(userDto);
    }

    public MeasurementDto measurementInfo(Long userId) {
        return userRepository.measurementInfo(userId);
    }

    @Transactional
    public void measurementUpdate(MeasurementDto measurementDto, Long userId) {
        Measurement measurement = measurementRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResultException("measurement dosen't exist"));
        measurement.update(measurementDto);
    }

    public List<ProductPreviewDto> recentProduct(Long userId) {
        return recentRepository.recentProduct(userId);
    }
}
