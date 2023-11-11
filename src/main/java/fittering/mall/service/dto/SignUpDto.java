package fittering.mall.service.dto;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
    private String username;
    private String password;
    private String email;
    private String gender;
    private Integer year;
    private Integer month;
    private Integer day;
}