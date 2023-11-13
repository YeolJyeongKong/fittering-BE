package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestLoginDto {
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;
}
