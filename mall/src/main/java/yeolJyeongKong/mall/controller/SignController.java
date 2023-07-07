package yeolJyeongKong.mall.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.config.jwt.JwtTokenProvider;
import yeolJyeongKong.mall.domain.dto.LoginDto;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.entity.User;
import yeolJyeongKong.mall.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/login")
    @ResponseBody
    public String loginFromAPI(@RequestBody LoginDto loginDto) {
        User user = userService.login(loginDto);
        return user != null ?
                jwtTokenProvider.createToken(user.getEmail(), user.getRoles()) : "일치하는 유저 정보가 없습니다.";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("LoginUserDto", new SignUpDto());
        return "login";
    }

    /**
     * 로그인 테스트용
     */
    @PostMapping("/login")
    public String login(@ModelAttribute("LoginUserDto") SignUpDto userDto, BindingResult result) {
        boolean login = userService.login(userDto);
        log.info("[로그인 결과] {}", login);
        return "test";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("userDto", new SignUpDto());
        return "signUp";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("userDto") SignUpDto userDto, BindingResult result) {
        if(result.hasErrors()) {
            return "signUp";
        }

        if (userService.usernameExist(userDto.getUsername())) {
            result.rejectValue("username", "username.exist", "같은 이름이 존재합니다.");
            return "signUp";
        }

        if (userService.emailExist(userDto.getEmail())) {
            result.rejectValue("email", "email.exist", "같은 이메일이 존재합니다.");
            return "signUp";
        }

        userService.save(userDto);
        return "redirect:/login";
    }
}
