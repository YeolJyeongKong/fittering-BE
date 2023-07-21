package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDto {
    private Integer price;
    private String name;
    private String gender;
    private Integer type;
    private String image;
    private String descriptionImage;
    private String categoryName;
    private String mallName;
    private List<TopDto> topSizes;
    private List<BottomDto> bottomSizes;
}
