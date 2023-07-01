package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MallPreviewDto {

    private String name;
    private String url;
    private String image;

    @QueryProjection
    public MallPreviewDto(String name, String url, String image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }
}
