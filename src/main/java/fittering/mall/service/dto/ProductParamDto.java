package fittering.mall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductParamDto {
    private String productName;
    private Long mallId;
    private Long categoryId;
    private Long subCategoryId;
    private Long filterId;
    private String gender;
}
