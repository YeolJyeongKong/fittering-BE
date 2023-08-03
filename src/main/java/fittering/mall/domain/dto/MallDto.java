package fittering.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fittering.mall.domain.entity.Mall;

import java.util.List;

@Getter
@NoArgsConstructor
public class MallDto {

    private Long id;
    private String name;
    private String url;
    private String image;
    private String description;
    private Integer view;
    private List<MallRankProductDto> products;

    @QueryProjection
    public MallDto(Long id, String name, String url, String image,
                   String description, Integer view, List<MallRankProductDto> products) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.image = image;
        this.description = description;
        this.view = view;
        this.products = products;
    }

    public MallDto(Mall mall) {
        id = mall.getId();
        name = mall.getName();
        url = mall.getUrl();
        image = mall.getImage();
        description = mall.getDescription();
        view = mall.getRank().getView();
    }
}
