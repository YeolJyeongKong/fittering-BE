package fittering.mall.controller;

import fittering.mall.config.auth.domain.AppleServiceResponse;
import fittering.mall.config.jwt.JwtTokenProvider;
import fittering.mall.domain.entity.User;
import fittering.mall.service.AppleService;
import fittering.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private final AppleService appleService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login/apple")
    public String appleServiceRedirect(AppleServiceResponse appleServiceResponse) {
        if (appleServiceResponse == null) {
            return "redirect:https://fit-tering.com/login";
        }

        String email = appleService.getEmail(appleServiceResponse.getId_token());
        User user = userService.findByEmail(email);

        if (user == null) {
            User newUser = appleService.saveUser(email, "apple");
            return "redirect:https://fit-tering.com/login?token=" + jwtTokenProvider.createToken(newUser.getEmail(), newUser.getRoles());
        }
        return "redirect:https://fit-tering.com/login?token=" + jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }
}
