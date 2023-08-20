package fittering.mall.config;

import java.util.List;

public class AcceptedUrl {
    public final static List<String> ACCEPTED_URL_LIST = List.of(
            "/login", "/signup", "/error",
            "/api/v1/login", "/api/v1/signup",
            "/swagger-ui.html", "/swagger-ui/index.html", "/swagger-ui/index.css", "/swagger-ui/swagger-ui.css",
            "/swagger-ui/swagger-ui-bundle.js", "/swagger-ui/swagger-ui-standalone-preset.js", "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/favicon-32x32.png", "/swagger-ui/favicon-16x16.png", "/api-docs/json/swagger-config",
            "/api-docs/json", "/actuator/prometheus",
            "/login/oauth/apple", "/login/oauth/google", "/login/oauth/kakao",
            "/login/apple", "/login/google", "/login/kakao"
    );
}
