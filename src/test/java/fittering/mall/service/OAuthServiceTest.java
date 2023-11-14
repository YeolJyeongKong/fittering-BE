package fittering.mall.service;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.User;
import fittering.mall.repository.MeasurementRepository;
import fittering.mall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OAuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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