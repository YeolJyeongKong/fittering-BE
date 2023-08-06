package fittering.mall.domain.dto.controller.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestSignUpDto {
    private String username;
    private String password;
    private String email;
    private String gender;
    private Integer year;
    private Integer month;
    private Integer day;
}
