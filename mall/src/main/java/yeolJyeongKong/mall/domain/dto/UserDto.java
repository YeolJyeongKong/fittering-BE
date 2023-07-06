package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserDto {
    private String email;
    private String username;
    private String gender;
    private Integer year;
    private Integer month;
    private Integer day;

    @QueryProjection
    public UserDto(String email, String username, String gender, Integer year, Integer month, Integer day) {
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
