package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestLoginDto {
    @Email
    private String email;
    @NonNull
    private String password;
}
