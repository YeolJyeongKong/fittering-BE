package fittering.mall.domain.dto.repository;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SavedUserDto {
    private String email;
    private String username;
    private String gender;
    private Integer year;
    private Integer month;
    private Integer day;

    @QueryProjection
    public SavedUserDto(String email, String username, String gender, Integer year, Integer month, Integer day) {
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
