package fittering.mall.service.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class MallNameAndIdDto {
    private Long id;
    private String name;

    @QueryProjection
    public MallNameAndIdDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
