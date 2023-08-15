package fittering.mall.domain.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OuterProductDto {
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
    private Integer view;
    private String popularGender;
    private Integer popularAgeRange;
    private List<OuterDto> sizes;
}
