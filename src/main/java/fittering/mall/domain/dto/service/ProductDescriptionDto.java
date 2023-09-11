package fittering.mall.domain.dto.service;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ProductDescriptionDto {
    private String url;

    @QueryProjection
    public ProductDescriptionDto(String url) {
        this.url = url;
    }
}
