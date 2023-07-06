package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import yeolJyeongKong.mall.domain.dto.MeasurementDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.dto.UserDto;
import yeolJyeongKong.mall.domain.entity.Measurement;
import yeolJyeongKong.mall.domain.entity.User;
import yeolJyeongKong.mall.repository.MeasurementRepository;
import yeolJyeongKong.mall.repository.RecentRepository;
import yeolJyeongKong.mall.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserService {

    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;
    private final RecentRepository recentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void save(SignUpDto signUpDto) {
        User user = new User(signUpDto, passwordEncoder.encode(signUpDto.getPassword()));
        userRepository.save(user);
    }

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

    public boolean login(SignUpDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new NoResultException("User doesn't exist"));

        return passwordEncoder.matches(userDto.getPassword(), user.getPassword());
    }

    public boolean usernameExist(String username) {
        return userRepository.usernameCount(username) != 0L;
    }

    public boolean emailExist(String email) {
        return userRepository.emailCount(email) != 0L;
    }
}
