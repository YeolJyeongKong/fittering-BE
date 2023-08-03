package fittering.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class ProductPreviewDto {

    private Long productId;
    private String productImage;
    private String productName;
    private Integer price;
    private String mallName;
    private String mallUrl;

    @QueryProjection
    public ProductPreviewDto(Long productId, String productImage, String productName,
                             Integer price, String mallName, String mallUrl) {
        this.productId = productId;
        this.productImage = productImage;
        this.productName = productName;
        this.price = price;
        this.mallName = mallName;
        this.mallUrl = mallUrl;
    }
}
