package fittering.mall.domain.dto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDto {
    private String email;
    private String username;
    private String gender;
    private Integer year;
    private Integer month;
    private Integer day;
}
