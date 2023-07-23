package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.*;
import yeolJyeongKong.mall.repository.*;

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
    private final FavoriteRepository favoriteRepository;
    private final MallRepository mallRepository;
    private final RankRepository rankRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final RecentRecommendationRepository recentRecommendationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User save(SignUpDto signUpDto) {
        Measurement measurement = measurementRepository.save(new Measurement());
        User user = new User(signUpDto, passwordEncoder.encode(signUpDto.getPassword()), measurement);
        measurement.setUser(user);
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user dosen't exist"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
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
    public Recent saveRecentProduct(Long userId, Long productId) {
        Optional<Recent> recent = recentRepository.findByUserId(userId);
        User user = findById(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));

        if(recent.isEmpty()) {
            Recent newRecent = recentRepository.save(new Recent(user, product));
            product.setRecent(newRecent);
            return newRecent;
        }

        recent.get().update(product);
        return recent.get();
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
        return userRepository.existsByEmail(email);
    }

    public String getTmpPassword() {
        char[] charSet = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String newPassword = "";

        for (int i = 0; i < 10; i++) {
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

        for (Favorite favorite : user.getFavorites()) {
            if (favorite.getProduct() != null) {
                Product product = productRepository.findById(favorite.getProduct().getId()).get();
                product.getFavorites().remove(favorite);
                continue;
            }

            Mall mall = mallRepository.findById(favorite.getMall().getId()).get();
            mall.getFavorites().remove(favorite);
        }
        favoriteRepository.deleteByUserId(userId);

        measurementRepository.deleteByUserId(userId);

        user.getRanks().forEach(rank -> {
            Mall mall = mallRepository.findById(rank.getMall().getId())
                    .orElseThrow(() -> new NoResultException("mall dosen't exist"));
            mall.deleteRank();
        });
        rankRepository.deleteByUserId(userId);

        Recent recent = recentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoResultException("recent dosen't exist"));
        recent.getProducts().forEach(Product::deleteRecent);
        recentRepository.deleteById(recent.getId());

        UserRecommendation userRecommendation = userRecommendationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoResultException("user recommendation dosen't exist"));
        userRecommendation.getProducts().forEach(Product::deleteUserRecommendation);
        userRecommendationRepository.deleteById(userRecommendation.getId());

        RecentRecommendation recentRecommendation = recentRecommendationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoResultException("recent recommendation dosen't exist"));
        recentRecommendation.getProducts().forEach(Product::deleteRecentRecommendation);
        recentRecommendationRepository.deleteById(recentRecommendation.getId());

        userRepository.delete(user);
    }
}
