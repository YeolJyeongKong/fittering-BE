package fittering.mall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DressProductDto {
    private Long favoriteCount;
    private String productImage;
    private String productName;
    private String productGender;
    private Integer price;
    private String mallName;
    private String mallUrl;
    private String mallImage;
    private String origin;
    private String category;
    private String subCategory;
    private Integer view;
    private String popularGender;
    private Integer popularAgeRange;
    private List<DressDto> sizes;
}
