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

    private static final String MAIN_LOGIN_URL = "https://fit-tering.com/login";
    private static final String APPLE_AUTH_URL = "https://appleid.apple.com/auth/authorize";
    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    @GetMapping("/login/oauth/apple")
    public String loginAppleOAuth() {
        return loginUrl(APPLE_AUTH_URL, APPLE_CLIENT_ID, APPLE_REDIRECT_URI, APPLE_RESPONSE_TYPE, APPLE_NONCE);
    }

    @GetMapping("/login/apple")
    public String appleServiceRedirect(AppleServiceResponse appleServiceResponse) {
        if (appleServiceResponse == null) {
            return "redirect:" + MAIN_LOGIN_URL;
        }

        String email = oAuthService.getEmail(appleServiceResponse.getId_token());
        User user = userService.findByEmail(email);

        if (user == null) {
            User newUser = oAuthService.saveUser(email, "apple");
            return redirectWithToken(MAIN_LOGIN_URL, jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles()));
        }
        return redirectWithToken(MAIN_LOGIN_URL, jwtTokenProvider.createToken(user.getEmail(), user.getRoles()));
    }

    @GetMapping("/login/oauth/kakao")
    public String loginKakaoOAuth() {
        return loginUrl(KAKAO_AUTH_URL, KAKAO_CLIENT_ID, KAKAO_REDIRECT_URI, KAKAO_RESPONSE_TYPE, KAKAO_SCOPE);
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
            return redirectWithToken(MAIN_LOGIN_URL, jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles()));
        }
        return redirectWithToken(MAIN_LOGIN_URL, jwtTokenProvider.createToken(user.getEmail(), user.getRoles()));
    }

    @GetMapping("/login/oauth/google")
    public String loginGoogleOAuth() {
        return loginUrl(GOOGLE_AUTH_URL, GOOGLE_CLIENT_ID, GOOGLE_REDIRECT_URI, GOOGLE_RESPONSE_TYPE, GOOGLE_SCOPE);
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
            return redirectWithToken(MAIN_LOGIN_URL, jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles()));
        }
        return redirectWithToken(MAIN_LOGIN_URL, jwtTokenProvider.createToken(user.getEmail(), user.getRoles()));
    }

    private static String loginUrl(String authUrl, String clientId, String redirectUri, String responseType, String nonce) {
        return "redirect:" + authUrl
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=" + responseType
                + "&nonce=" + nonce;
    }

    private static String redirectWithToken(String mainUrl, String token) {
        return "redirect:" + mainUrl
                + "?token=" + token;
    }
}
