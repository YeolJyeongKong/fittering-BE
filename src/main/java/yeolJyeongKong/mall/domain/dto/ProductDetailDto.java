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
    private String categoryName;
    private String subCategoryName;
    private String mallName;
    private List<OuterDto> outerSizes;
    private List<TopDto> topSizes;
    private List<DressDto> dressSizes;
    private List<BottomDto> bottomSizes;
    private List<String> descriptionImages;
}
