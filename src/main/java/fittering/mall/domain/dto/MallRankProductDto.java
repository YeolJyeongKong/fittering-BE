package fittering.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MallRankProductDto {

    private Long productId;
    private String productImage;

    @QueryProjection
    public MallRankProductDto(Long productId, String productImage) {
        this.productId = productId;
        this.productImage = productImage;
    }
}
