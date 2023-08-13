package fittering.mall.controller;

import fittering.mall.config.auth.domain.AppleServiceResponse;
import fittering.mall.config.auth.domain.KakaoServiceResponse;
import fittering.mall.config.jwt.JwtTokenProvider;
import fittering.mall.domain.entity.User;
import fittering.mall.service.OAuthService;
import fittering.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final RestTemplate restTemplate;
    private final OAuthService oAuthService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;
    @Value("${kakao.response-type}")
    private String KAKAO_RESPONSE_TYPE;
    @Value("${kakao.scope}")
    private String KAKAO_SCOPE;
    @Value("${kakao.grant-type}")
    private String KAKAO_GRANT_TYPE;

    @PostMapping("/login/apple")
    public String appleServiceRedirect(AppleServiceResponse appleServiceResponse) {
        if (appleServiceResponse == null) {
            return "redirect:https://fit-tering.com/login";
        }

        String email = oAuthService.getEmail(appleServiceResponse.getId_token());
        User user = userService.findByEmail(email);

        if (user == null) {
            User newUser = oAuthService.saveUser(email, "apple");
            return "redirect:https://fit-tering.com/login?token=" + jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles());
        }
        return "redirect:https://fit-tering.com/login?token=" + jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    @GetMapping("/login/oauth/kakao")
    public String loginKakaoOAuth() {
        return "redirect:https://kauth.kakao.com/oauth/authorize?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI
                + "&response_type=" + KAKAO_RESPONSE_TYPE
                + "&scope=" + KAKAO_SCOPE;
    }

    @GetMapping("/login/kakao")
    public String kakaoServiceRedirect(@RequestParam String code) {
        URI uri = UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/token")
                .build()
                .toUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", KAKAO_GRANT_TYPE);
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("client_secret", KAKAO_CLIENT_SECRET);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);
        KakaoServiceResponse response = restTemplate.postForObject(uri, params, KakaoServiceResponse.class);

        String email = oAuthService.getEmail(response.getId_token());
        User user = userService.findByEmail(email);

        if (user == null) {
            User newUser = oAuthService.saveUser(email, "kakao");
            return "redirect:https://fit-tering.com/login?token=" + jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles());
        }
        return "redirect:https://fit-tering.com/login?token=" + jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }
}
