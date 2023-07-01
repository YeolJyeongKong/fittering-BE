package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCategoryDto {
    private String category;
    private Long count;
}
