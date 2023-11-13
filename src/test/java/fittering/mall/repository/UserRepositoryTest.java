package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Measurement;
import fittering.mall.domain.entity.User;
import fittering.mall.service.dto.MeasurementDto;
import fittering.mall.service.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @DisplayName("이메일로 유저를 조회할 수 있다.")
    @Test
    void findUserByEmail() {
        //given
        User user = userRepository.save(createUser());

        //when
        Optional<User> target = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(target).isNotEmpty();
        assertThat(target.get())
                .extracting("id", "username", "password", "email", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("Provider Login id 기준으로 유저를 조회할 수 있다.")
    @Test
    void findUserByProviderLoginId() {
        //given
        User user = userRepository.save(createUser());

        //when
        Optional<User> target = userRepository.findByProviderLoginId(user.getProviderLoginId());

        //then
        assertThat(target).isNotEmpty();
        assertThat(target.get())
                .extracting("id", "username", "password", "email", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("이메일 기준으로 유저가 존재하는지 확인할 수 있다.")
    @Test
    void existsUserByEmail() {
        //given
        User user = userRepository.save(createUser());

        //when
        Boolean target = userRepository.existsByEmail(user.getEmail());
        Boolean target2 = userRepository.existsByEmail(user.getEmail() + "1");

        //then
        assertThat(target).isTrue();
        assertThat(target2).isFalse();
    }

    @DisplayName("닉네임 기준으로 유저가 존재하는지 확인할 수 있다.")
    @Test
    void existsUserByUsername() {
        //given
        User user = userRepository.save(createUser());

        //when
        Boolean target = userRepository.existsByUsername(user.getUsername());
        Boolean target2 = userRepository.existsByUsername(user.getUsername() + "1");

        //then
        assertThat(target).isTrue();
        assertThat(target2).isFalse();
    }

    @DisplayName("유저 id 기준으로 유저 정보를 조회할 수 있다.")
    @Test
    void getUserInfoByUserId() {
        //given
        User user = userRepository.save(createUser());

        //when
        UserDto target = userRepository.info(user.getId());

        //then
        assertThat(target)
                .extracting("email", "username", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getEmail(),
                        user.getUsername(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("유저 id 기준으로 유저 체형 정보를 조회할 수 있다.")
    @Test
    void getUserMeasurementByUserId() {
        //given
        Measurement measurement = measurementRepository.save(Measurement.builder()
                .height(100.0)
                .weight(100.0)
                .arm(100.0)
                .leg(100.0)
                .shoulder(100.0)
                .waist(100.0)
                .chest(100.0)
                .thigh(100.0)
                .hip(100.0)
                .build());
        User user = userRepository.save(createUser(measurement));

        //when
        MeasurementDto target = userRepository.measurementInfo(user.getId());

        //then
        assertThat(target)
                .extracting("height", "weight", "arm", "leg", "shoulder", "waist", "chest", "thigh", "hip")
                .containsExactlyInAnyOrder(
                        measurement.getHeight(),
                        measurement.getWeight(),
                        measurement.getArm(),
                        measurement.getLeg(),
                        measurement.getShoulder(),
                        measurement.getWaist(),
                        measurement.getChest(),
                        measurement.getThigh(),
                        measurement.getHip()
                );
    }

    @DisplayName("유저 id 기준으로 유저 성별을 조회할 수 있다.")
    @Test
    void getGenderByUserId() {
        //given
        User user = userRepository.save(createUser());

        //when
        String target = userRepository.findGenderById(user.getId());

        //then
        assertThat(target).isEqualTo(user.getGender());
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
                .providerLoginId("provider")
                .roles(List.of("ROLE_USER"))
                .build();
    }

    private User createUser(Measurement measurement) {
        return User.builder()
                .username("testuser")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(0)
                .providerLoginId("provider")
                .roles(List.of("ROLE_USER"))
                .measurement(measurement)
                .build();
    }
}