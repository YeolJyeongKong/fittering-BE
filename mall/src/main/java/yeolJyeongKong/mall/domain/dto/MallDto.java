package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

@Getter
public class MallDto {

    private String name;
    private String url;
    private String image;
    private List<MallRankProductDto> products;

    @QueryProjection
    public MallDto(String name, String url, String image, List<MallRankProductDto> products) {
        this.name = name;
        this.url = url;
        this.image = image;
        this.products = products;
    }
}
