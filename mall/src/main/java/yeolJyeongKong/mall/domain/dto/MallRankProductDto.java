package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MallRankProductDto {

    private Long productId;
    private String productImage;

    @QueryProjection
    public MallRankProductDto(Long productId, String productImage) {
        this.productId = productId;
        this.productImage = productImage;
    }
}
