package fittering.mall.domain.dto.controller.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserDto {
    @Email
    private String email;
    @NonNull @Length(max = 20)
    private String username;
    @NonNull @Length(max = 1)
    private String gender;
    @NonNull
    private Integer year;
    @NonNull
    private Integer month;
    @NonNull
    private Integer day;
}
