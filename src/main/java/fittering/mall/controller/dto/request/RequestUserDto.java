package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserDto {
    @NotNull(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;
    @NotNull(message = "닉네임을 입력해주세요.")
    @Length(max = 20, message = "닉네임은 20자 이내로 입력해주세요.")
    private String username;
    @NotNull(message = "성별을 입력해주세요.")
    @Length(max = 1, message = "성별은 1자 이내로 입력해주세요.")
    private String gender;
    @NotNull(message = "태어난 년도를 입력해주세요.")
    private Integer year;
    @NotNull(message = "태어난 월을 입력해주세요.")
    private Integer month;
    @NotNull(message = "태어난 일자를 입력해주세요.")
    private Integer day;
}
