package fittering.mall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fittering.mall.service.dto.MailDto;
import fittering.mall.service.MailService;
import fittering.mall.service.UserService;

import static fittering.mall.controller.ControllerUtils.isInvalidEmail;

@Tag(name = "메일", description = "비밀번호 찾기 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MailController {

    private final UserService userService;
    private final MailService mailService;

    @Operation(summary = "유저 메일 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"이메일을 사용하는 유저가 존재합니다.\""))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"일치하는 메일이 없습니다.\"")))
    })
    @PostMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        if (isInvalidEmail(email)) {
            return new ResponseEntity<>("올바른 이메일 형식이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        if (!userService.emailExist(email)) {
            return new ResponseEntity<>("일치하는 메일이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("이메일을 사용하는 유저가 존재합니다.", HttpStatus.OK);
    }

    @Operation(summary = "임시 패스워드 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"비밀번호 발급이 완료되었습니다.\""))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"비밀번호 찾기는 1시간에 한 번 가능합니다.\"")))
    })
    @PostMapping("/password/send")
    public ResponseEntity<?> sendPassword(@RequestParam("email") String email) {
        if (isInvalidEmail(email)) {
            return new ResponseEntity<>("올바른 이메일 형식이 아닙니다.", HttpStatus.BAD_REQUEST);
        }
        if (!userService.updatePasswordToken(email)) {
            return new ResponseEntity<>("비밀번호 찾기는 1시간에 한 번 가능합니다.", HttpStatus.BAD_REQUEST);
        }

        String tmpPassword = userService.getTmpPassword();
        userService.updatePassword(tmpPassword, email);
        MailDto mail = mailService.createMail(tmpPassword, email);

        mailService.sendMail(mail);

        return new ResponseEntity<>("비밀번호 발급이 완료되었습니다.", HttpStatus.OK);
    }
}
