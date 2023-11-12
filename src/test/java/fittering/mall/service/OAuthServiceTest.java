package fittering.mall.service;

import fittering.mall.domain.entity.User;
import fittering.mall.repository.MeasurementRepository;
import fittering.mall.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class OAuthServiceTest {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        measurementRepository.deleteAllInBatch();
    }

    @DisplayName("소셜 로그인으로 접근한 유저 정보를 저장할 수 있다.")
    @Test
    void saveUserByOAuth() {
        //given
        String email = "test@email.com";
        String provider = "provider";

        //when
        User savedUser = oAuthService.saveUser(email, provider);

        //then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser)
                .extracting("username", "email", "provider")
                .containsExactlyInAnyOrder(
                        email.substring(0, email.indexOf('@')),
                        email,
                        provider
                );
    }
}