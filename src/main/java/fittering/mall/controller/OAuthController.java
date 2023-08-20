package fittering.mall.controller;

import fittering.mall.config.auth.domain.AppleServiceResponse;
import fittering.mall.config.auth.domain.GoogleServiceResponse;
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

    @Value("${apple.client-id}")
    private String APPLE_CLIENT_ID;
    @Value("${apple.redirect-uri}")
    private String APPLE_REDIRECT_URI;
    @Value("${apple.response_type}")
    private String APPLE_RESPONSE_TYPE;
    @Value("${apple.nonce}")
    private String APPLE_NONCE;

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

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;
    @Value("${google.response-type}")
    private String GOOGLE_RESPONSE_TYPE;
    @Value("${google.scope}")
    private String GOOGLE_SCOPE;
    @Value("${google.grant-type}")
    private String GOOGLE_GRANT_TYPE;

    private final String MAIN_LOGIN_URL = "https://fit-tering.com/login";
    private final String APPLE_AUTH_URL = "https://appleid.apple.com/auth/authorize";
    private final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    @GetMapping("/login/oauth/apple")
    public String loginAppleOAuth() {
        return "redirect:" + APPLE_AUTH_URL
                + "?client_id=" + APPLE_CLIENT_ID
                + "&redirect_uri=" + APPLE_REDIRECT_URI
                + "&response_type=" + APPLE_RESPONSE_TYPE
                + "&nonce=" + APPLE_NONCE;
    }

    @PostMapping("/login/apple")
    public String appleServiceRedirect(AppleServiceResponse appleServiceResponse) {
        if (appleServiceResponse == null) {
            return "redirect:" + MAIN_LOGIN_URL;
        }

        String email = oAuthService.getEmail(appleServiceResponse.getId_token());
        User user = userService.findByEmail(email);

        if (user == null) {
            User newUser = oAuthService.saveUser(email, "apple");
            return "redirect:" + MAIN_LOGIN_URL
                    + "?token=" + jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles());
        }
        return "redirect:" + MAIN_LOGIN_URL
                + "?token=" + jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    @GetMapping("/login/oauth/kakao")
    public String loginKakaoOAuth() {
        return "redirect:" + KAKAO_AUTH_URL
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI
                + "&response_type=" + KAKAO_RESPONSE_TYPE
                + "&scope=" + KAKAO_SCOPE;
    }

    @GetMapping("/login/kakao")
    public String kakaoServiceRedirect(@RequestParam String code) {
        URI uri = UriComponentsBuilder.fromUriString(KAKAO_TOKEN_URL)
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
            return "redirect:" + MAIN_LOGIN_URL
                    + "?token=" + jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles());
        }
        return "redirect:" + MAIN_LOGIN_URL
                + "?token=" + jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    @GetMapping("/login/oauth/google")
    public String loginGoogleOAuth() {
        return "redirect:" + GOOGLE_AUTH_URL
                + "?client_id=" + GOOGLE_CLIENT_ID
                + "&redirect_uri=" + GOOGLE_REDIRECT_URI
                + "&response_type=" + GOOGLE_RESPONSE_TYPE
                + "&scope=" + GOOGLE_SCOPE;
    }

    @GetMapping("/login/google")
    public String googleServiceRedirect(@RequestParam String code) {
        URI uri = UriComponentsBuilder.fromUriString(GOOGLE_TOKEN_URL)
                .build()
                .toUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GOOGLE_GRANT_TYPE);
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_SECRET);
        params.add("redirect_uri", GOOGLE_REDIRECT_URI);
        params.add("code", code);
        GoogleServiceResponse response = restTemplate.postForObject(uri, params, GoogleServiceResponse.class);

        String email = oAuthService.getEmail(response.getId_token());
        User user = userService.findByEmail(email);

        if (user == null) {
            User newUser = oAuthService.saveUser(email, "google");
            return "redirect:" + MAIN_LOGIN_URL
                    + "?token=" + jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles());
        }
        return "redirect:" + MAIN_LOGIN_URL
                + "?token=" + jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }
}
