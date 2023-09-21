package fittering.mall.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ResponseProductPreviewDto {
    private Long productId;
    private String productImage;
    private String productName;
    private Integer price;
    private String mallName;
    private String mallUrl;

    @QueryProjection
    public ResponseProductPreviewDto(Long productId, String productImage, String productName,
                                     Integer price, String mallName, String mallUrl) {
        this.productId = productId;
        this.productImage = productImage;
        this.productName = productName;
        this.price = price;
        this.mallName = mallName;
        this.mallUrl = mallUrl;
    }
}
