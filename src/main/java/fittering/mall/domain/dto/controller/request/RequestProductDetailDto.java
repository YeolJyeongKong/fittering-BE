package fittering.mall.domain.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductDetailDto {
    private Integer price;
    private String name;
    private String gender;
    private Integer type;
    private String image;
    private String origin;
    private String categoryName;
    private String subCategoryName;
    private String mallName;
    private List<RequestOuterDto> outerSizes;
    private List<RequestTopDto> topSizes;
    private List<RequestDressDto> dressSizes;
    private List<RequestBottomDto> bottomSizes;
    private List<String> productDescriptions;
}
