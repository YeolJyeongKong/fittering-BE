package fittering.mall.controller;

import fittering.mall.domain.dto.controller.request.RequestLoginDto;
import fittering.mall.domain.dto.controller.request.RequestSignUpDto;
import fittering.mall.domain.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import fittering.mall.config.jwt.JwtTokenProvider;
import fittering.mall.domain.dto.service.SignUpDto;
import fittering.mall.domain.entity.User;
import fittering.mall.service.UserService;

import static fittering.mall.controller.ControllerUtils.getValidationErrorResponse;

@Tag(name = "로그인/회원가입", description = "로그인/회원가입 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SignController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "로그인")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "{token}")))
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid RequestLoginDto requestLoginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        User user = userService.login(UserMapper.INSTANCE.toLoginDto(requestLoginDto));
        if(user == null) {
            return new ResponseEntity<>("일치하는 유저 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(jwtTokenProvider.createToken(user.getEmail(), user.getRoles()), HttpStatus.OK);
    }

    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = SignUpDto.class)))
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid RequestSignUpDto requestSignUpDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        if (userService.usernameExist(requestSignUpDto.getUsername())) {
            return new ResponseEntity<>("같은 이름이 존재합니다.", HttpStatus.BAD_REQUEST);
        }
        if (userService.emailExist(requestSignUpDto.getEmail())) {
            return new ResponseEntity<>("같은 이메일이 존재합니다.", HttpStatus.BAD_REQUEST);
        }

        userService.save(UserMapper.INSTANCE.toSignUpDto(requestSignUpDto));
        return new ResponseEntity<>(requestSignUpDto, HttpStatus.CREATED);
    }
}
