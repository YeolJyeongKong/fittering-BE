package fittering.mall.service;

import fittering.mall.controller.dto.request.RequestRecommendProductDto;
import fittering.mall.controller.dto.request.RequestUserDto;
import fittering.mall.controller.dto.response.ResponseMeasurementDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.controller.dto.response.ResponseUserDto;
import fittering.mall.service.dto.LoginDto;
import fittering.mall.service.dto.MeasurementDto;
import fittering.mall.service.dto.SignUpDto;
import fittering.mall.domain.mapper.MeasurementMapper;
import fittering.mall.domain.mapper.UserMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fittering.mall.domain.entity.User.getAgeRange;

@Slf4j
@Service
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserService {

    private static final int TEMP_PASSWORD_LENGTH = 10;

    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;
    private final RecentRepository recentRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final MallRepository mallRepository;
    private final RankRepository rankRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final RecentRecommendationRepository recentRecommendationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User save(SignUpDto signUpDto) {
        Measurement measurement = measurementRepository.save(new Measurement());
        User user = UserMapper.INSTANCE.toUser(signUpDto, getAgeRange(signUpDto.getYear(), signUpDto.getMonth(), signUpDto.getDay()),
                passwordEncoder.encode(signUpDto.getPassword()), measurement, new ArrayList<>(List.of("ROLE_USER")));
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user dosen't exist"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public ResponseUserDto info(Long userId) {
        return UserMapper.INSTANCE.toResponseUserDto(userRepository.info(userId));
    }

    @Transactional
    public void infoUpdate(RequestUserDto requestUserDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user dosen't exist"));
        user.update(UserMapper.INSTANCE.toUserDto(requestUserDto));
    }

    public boolean passwordCheck(Long userId, String inputPassword) {
        String password = findById(userId).getPassword();
        return passwordEncoder.matches(inputPassword, password);
    }

    public void setPassword(Long userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public ResponseMeasurementDto measurementInfo(Long userId) {
        MeasurementDto measurementDto = userRepository.measurementInfo(userId);
        return MeasurementMapper.INSTANCE.toResponseMeasurementDto(measurementDto);
    }

    @Transactional
    public void measurementUpdate(MeasurementDto measurementDto, Long userId) {
        Measurement measurement = measurementRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResultException("measurement dosen't exist"));
        measurement.update(measurementDto);
    }

    public List<ResponseProductPreviewDto> recentProductPreview(Long userId) {
        return recentRepository.recentProductPreview(userId);
    }

    public Page<ResponseProductPreviewDto> recentProduct(Long userId, Pageable pageable) {
        return recentRepository.recentProduct(userId, pageable);
    }

    public RequestRecommendProductDto recentProductIds(Long userId) {
        List<Long> productIds = recentRepository.recentProductIds(userId);
        String gender = userRepository.findGenderById(userId);
        return RequestRecommendProductDto.builder()
                .product_ids(productIds)
                .gender(gender)
                .build();
    }

    @Transactional
    public Recent saveRecentProduct(Long userId, Long productId) {
        User user = findById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));
        return recentRepository.save(Recent.builder()
                                        .timestamp(LocalDateTime.now())
                                        .user(user)
                                        .product(product)
                                        .build());
    }

    public User login(LoginDto loginDto) {
        Optional<User> optionalUser = userRepository.findByEmail(loginDto.getEmail());
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        return passwordEncoder.matches(loginDto.getPassword(), user.getPassword()) ? user : null;
    }

    public boolean usernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public String getTmpPassword() {
        char[] charSet = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String newPassword = "";

        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int idx = (int) (charSet.length * Math.random());
            newPassword += charSet[idx];
        }

        return newPassword;
    }

    @Transactional
    public void updatePassword(String tmpPassword, String memberEmail) {
        String encryptedPassword = passwordEncoder.encode(tmpPassword);
        User user = userRepository.findByEmail(memberEmail).orElseThrow(() ->
                new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        user.setPassword(encryptedPassword);
    }

    @Transactional
    public boolean updatePasswordToken(String email) {
        User user = findByEmail(email);
        return user.updatePasswordToken();
    }

    @Transactional
    public void delete(Long userId) {
        User user = findById(userId);

        user.getRoles().clear();

        user.getFavorites().forEach(favorite -> {
            removeFavoriteOfOtherEntity(favorite);
        });

        favoriteRepository.deleteByUserId(userId);
        measurementRepository.deleteByUserId(userId);
        rankRepository.deleteByUserId(userId);

        List<Recent> recents = recentRepository.findByUserId(user.getId());
        recents.forEach(recent -> {
            recent.getProduct().getRecents().remove(recent);
            recentRepository.deleteById(recent.getId());
        });

        userRecommendationRepository.findByUserId(user.getId()).forEach(userRecommendation -> {
            userRecommendation.getProduct().getUserRecommendations().remove(userRecommendation);
            userRecommendationRepository.deleteById(userRecommendation.getId());
        });

        recentRecommendationRepository.findByUserId(user.getId()).forEach(recentRecommendation -> {
            recentRecommendation.getProduct().getRecentRecommendations().remove(recentRecommendation);
            recentRecommendationRepository.deleteById(recentRecommendation.getId());
        });

        userRepository.delete(user);
    }

    @Transactional
    public void updateRecentLastInitializedAtOfUsers() {
        userRepository.findAll().forEach(user -> {
            updateRecentLastInitializedAtOfUser(user);
        });
    }

    @Transactional
    public void resetUpdatedAtOfUserRecommendation() {
        userRepository.findAll().forEach(user -> {
            userRecommendationRepository.deleteByUserId(user.getId());
        });
    }

    @Transactional
    public void resetUpdatedAtOfRecentRecommendation() {
        userRepository.findAll().forEach(user -> {
            recentRecommendationRepository.deleteByUserId(user.getId());
        });
    }

    public boolean isValidEmailAndPassword(String email, String password) {
        User user = findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    public boolean isSameUser(Long userId, String email) {
        User user = findById(userId);
        return user.getEmail().equals(email);
    }

    private void updateRecentLastInitializedAtOfUser(User user) {
        if (user.getRecentLastInitializedAt() == null) {
            user.updateRecentLastInitializedAt();
            return;
        }

        LocalDateTime initializeAt = user.getRecentLastInitializedAt();
        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.WEEKS.between(initializeAt, now) >= 1) {
            user.updateRecentLastInitializedAt();
            recentRepository.initializeRecents(user.getId());
        }
    }

    private void removeFavoriteOfOtherEntity(Favorite favorite) {
        if (favorite.getProduct() != null) {
            Product product = productRepository.findById(favorite.getProduct().getId()).get();
            product.getFavorites().remove(favorite);
            return;
        }

        Mall mall = mallRepository.findById(favorite.getMall().getId()).get();
        mall.getFavorites().remove(favorite);
    }
}
