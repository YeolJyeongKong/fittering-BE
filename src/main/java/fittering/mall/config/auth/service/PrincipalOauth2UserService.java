package fittering.mall.config.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import fittering.mall.config.auth.domain.GoogleUserInfo;
import fittering.mall.config.auth.domain.KakaoUserInfo;
import fittering.mall.config.auth.domain.Oauth2UserInfo;
import fittering.mall.domain.entity.Measurement;
import fittering.mall.domain.entity.User;
import fittering.mall.repository.MeasurementRepository;
import fittering.mall.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fittering.mall.domain.entity.User.getAgeRange;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private static final String DEFAULT_GENDER = "M";
    private static final Integer DEFAULT_YEAR = 2023;
    private static final Integer DEFAULT_MONTH = 1;
    private static final Integer DEFAULT_DAY = 1;

    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserInfo oAuth2UserInfo = getOauth2UserInfo(provider, oAuth2User);

        String providerId = oAuth2UserInfo.getProviderId();
        String loginId = provider + "_" + providerId;
        String email = oAuth2UserInfo.getEmail();

        Optional<User> optionalUser = userRepository.findByProviderLoginId(loginId);
        User user;

        if (optionalUser.isEmpty()) {
            Measurement measurement = measurementRepository.save(new Measurement());
            user = userRepository.save(User.builder()
                                        .username(email.substring(0, email.indexOf('@')))
                                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                        .email(email)
                                        .gender(DEFAULT_GENDER)
                                        .year(DEFAULT_YEAR)
                                        .month(DEFAULT_MONTH)
                                        .day(DEFAULT_DAY)
                                        .ageRange(getAgeRange(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY))
                                        .provider(provider)
                                        .providerId(providerId)
                                        .providerLoginId(provider + "_" + providerId)
                                        .measurement(measurement)
                                        .roles(new ArrayList<>(List.of("USER")))
                                        .build());
            return user;
        }

        user = optionalUser.get();
        return user;
    }

    public Oauth2UserInfo getOauth2UserInfo(String provider, OAuth2User oAuth2User) {
        if (provider.equals("google")) {
            return new GoogleUserInfo(oAuth2User.getAttributes());
        }
        if (provider.equals("kakao")) {
            return new KakaoUserInfo(oAuth2User.getAttributes());
        }
        return null;
    }
}
