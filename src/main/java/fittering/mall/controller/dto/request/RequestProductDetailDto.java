package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductDetailDto {
    @NotNull(message = "상품 가격은 필수입니다.")
    private Integer price;
    @Length(max = 50, message = "상품 이름은 50자 이하여야 합니다.")
    @NotNull(message = "상품 이름은 필수입니다.")
    private String name;
    @NotNull(message = "상품 성별 정보는 필수입니다.")
    @Length(max = 1, message = "상품 성별 정보는 1자 이하여야 합니다.")
    private String gender;
    @NotNull(message = "상품 종류는 필수입니다.")
    private Integer type;
    @NotNull(message = "상품 이미지는 필수입니다.")
    private String image;
    @NotNull(message = "상품 출처는 필수입니다.")
    private String origin;
    @NotNull(message = "상품 카테고리는 필수입니다.")
    private String categoryName;
    @NotNull(message = "상품 서브 카테고리는 필수입니다.")
    private String subCategoryName;
    @NotNull(message = "상품 쇼핑몰 이름은 필수입니다.")
    private String mallName;
    private List<RequestOuterDto> outerSizes;
    private List<RequestTopDto> topSizes;
    private List<RequestDressDto> dressSizes;
    private List<RequestBottomDto> bottomSizes;
    private List<String> productDescriptions;
}
