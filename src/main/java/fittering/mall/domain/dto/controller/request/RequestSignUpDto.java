package fittering.mall.domain.dto.controller.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestSignUpDto {
    @NonNull @Length(max = 20)
    private String username;
    @NonNull
    private String password;
    @Email
    private String email;
    @NonNull @Length(max = 1)
    private String gender;
    @NonNull
    private Integer year;
    @NonNull
    private Integer month;
    @NonNull
    private Integer day;
}
