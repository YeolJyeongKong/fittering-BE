package fittering.mall.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Hidden
@RestController
@RequestMapping("/swagger-ui")
public class SwaggerController {

    @GetMapping(path = "/swagger-ui.css", produces = "text/css")
    public String getCss() {
        String origin = toText(getClass().getResourceAsStream("/META-INF/resources/webjars/swagger-ui/4.18.2/swagger-ui.css"));
        String current = toText(getClass().getResourceAsStream("/swagger-ui.css"));
        return origin + current;
    }

    static String toText(InputStream in) {
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
    }
}
