package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ProductPreviewDto {

    private Long productId;
    private String productImage;
    private String productName;
    private Integer price;
    private String mallName;

    @QueryProjection
    public ProductPreviewDto(Long productId, String productImage, String productName, Integer price, String mallName) {
        this.productId = productId;
        this.productImage = productImage;
        this.productName = productName;
        this.price = price;
        this.mallName = mallName;
    }
}
