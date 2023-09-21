package fittering.mall.controller.dto.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductDetailDto {
    @NonNull
    private Integer price;
    @NonNull @Length(max = 50)
    private String name;
    @NonNull @Length(max = 1)
    private String gender;
    @NonNull
    private Integer type;
    @NonNull
    private String image;
    @NonNull
    private String origin;
    @NonNull
    private String categoryName;
    @NonNull
    private String subCategoryName;
    @NonNull
    private String mallName;
    private List<RequestOuterDto> outerSizes;
    private List<RequestTopDto> topSizes;
    private List<RequestDressDto> dressSizes;
    private List<RequestBottomDto> bottomSizes;
    private List<String> productDescriptions;
}
