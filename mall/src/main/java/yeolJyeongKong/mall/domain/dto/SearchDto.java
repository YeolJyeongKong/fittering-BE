package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchDto {
    private String keyword;
    private String gender;
}
