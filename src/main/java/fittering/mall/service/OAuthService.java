package fittering.mall.service;

import fittering.mall.config.auth.AppUtils;
import fittering.mall.domain.entity.Measurement;
import fittering.mall.domain.entity.User;
import fittering.mall.repository.MeasurementRepository;
import fittering.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static fittering.mall.domain.entity.User.getAgeRange;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final AppUtils appUtils;
    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String DEFAULT_GENDER = "M";
    private static final Integer DEFAULT_YEAR = 2023;
    private static final Integer DEFAULT_MONTH = 1;
    private static final Integer DEFAULT_DAY = 1;

    public User saveUser(String email, String provider) {
        Measurement measurement = measurementRepository.save(new Measurement());
        return userRepository.save(User.builder()
                .username(email.substring(0, email.indexOf('@')))
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .email(email)
                .gender(DEFAULT_GENDER)
                .year(DEFAULT_YEAR)
                .month(DEFAULT_MONTH)
                .day(DEFAULT_DAY)
                .ageRange(getAgeRange(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY))
                .provider(provider)
                .measurement(measurement)
                .roles(new ArrayList<>(List.of("USER")))
                .build());
    }

    public String getEmail(String id_token) {
        return appUtils.getEmailFromIdToken(id_token);
    }
}
