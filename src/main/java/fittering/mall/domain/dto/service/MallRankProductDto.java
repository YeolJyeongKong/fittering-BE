package fittering.mall.domain.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallRankProductDto {
    private Long productId;
    private String productImage;
}
