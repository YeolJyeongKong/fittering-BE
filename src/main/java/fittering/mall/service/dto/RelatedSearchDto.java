package fittering.mall.service.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class RelatedSearchDto {
    private Long id;
    private String name;
    private String image;

    @QueryProjection
    public RelatedSearchDto(Long id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
