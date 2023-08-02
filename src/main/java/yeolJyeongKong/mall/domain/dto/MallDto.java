package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MallDto {

    private Long id;
    private String name;
    private String image;
    private Integer view;
    private List<MallRankProductDto> products;

    @QueryProjection
    public MallDto(Long id, String name, String image, Integer view, List<MallRankProductDto> products) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.view = view;
        this.products = products;
    }
}
