package fittering.mall.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;

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
            user = userRepository.save(new User(email, provider, providerId, measurement));
            return new PrincipalDetails(user, oAuth2UserInfo);
        }

        user = optionalUser.get();
        return new PrincipalDetails(user, oAuth2UserInfo);
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
