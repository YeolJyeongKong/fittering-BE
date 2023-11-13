package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Measurement;
import fittering.mall.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class MeasurementRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저의 id로 체형 정보를 조회할 수 있다.")
    @Test
    void findByUserId() {
        //given
        User user = userRepository.save(createUser());

        //when
        Measurement target = measurementRepository.save(createMeasurement(user));

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target.getUser().getId()).isEqualTo(user.getId());
    }

    @DisplayName("유저의 id로 체형 정보를 삭제할 수 있다.")
    @Test
    void deleteByUserId() {
        //given
        User user = userRepository.save(createUser());
        Measurement measurement = measurementRepository.save(createMeasurement(user));

        //when
        measurementRepository.deleteByUserId(user.getId());

        //then
        Optional<Measurement> target = measurementRepository.findByUserId(user.getId());
        assertThat(target).isEmpty();
    }

    private User createUser() {
        return User.builder()
                .username("testuser")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(0)
                .roles(List.of("ROLE_USER"))
                .build();
    }

    private Measurement createMeasurement(User user) {
        return Measurement.builder()
                .height(100.0)
                .weight(100.0)
                .arm(100.0)
                .leg(100.0)
                .shoulder(100.0)
                .waist(100.0)
                .chest(100.0)
                .thigh(100.0)
                .hip(100.0)
                .user(user)
                .build();
    }
}