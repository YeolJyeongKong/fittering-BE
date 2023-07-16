package yeolJyeongKong.mall.service;

import com.querydsl.core.NonUniqueResultException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.Measurement;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.Recent;
import yeolJyeongKong.mall.domain.entity.User;
import yeolJyeongKong.mall.repository.MeasurementRepository;
import yeolJyeongKong.mall.repository.ProductRepository;
import yeolJyeongKong.mall.repository.RecentRepository;
import yeolJyeongKong.mall.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserService {

    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;
    private final RecentRepository recentRepository;
    private final ProductRepository productRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void save(SignUpDto signUpDto) {
        User user = new User(signUpDto, passwordEncoder.encode(signUpDto.getPassword()));
        userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user dosen't exist"));
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

    public boolean passwordCheck(Long userId, String inputPassword) {
        String password = findById(userId).getPassword();
        return passwordEncoder.matches(inputPassword, password);
    }

    public void setPassword(Long userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
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

    @Transactional
    public void saveRecentProduct(Long userId, Long productId) {
        Optional<Recent> recent = recentRepository.findByUserId(userId);
        User user = findById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));

        if(recent.isEmpty()) {
            recentRepository.save(new Recent(user, product));
            return;
        }

        recent.get().update(product);
    }

    @Transactional
    public void saveFavoriteProduct(Long userId, Long productId) {
        User user = findById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));

        user.getProducts().add(product);
    }

    @Transactional
    public void deleteFavoriteProduct(Long userId, Long productId) {
        User user = findById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));

        user.getProducts().remove(product);
    }

    public User login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NoResultException("User doesn't exist"));

        return passwordEncoder.matches(loginDto.getPassword(), user.getPassword()) ? user : null;
    }

    public boolean usernameExist(String username) {
        return userRepository.usernameCount(username) != 0L;
    }

    public boolean emailExist(String email) {
        return userRepository.emailCount(email) != 0L;
    }
}
