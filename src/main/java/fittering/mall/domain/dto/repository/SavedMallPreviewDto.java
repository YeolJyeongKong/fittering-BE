package fittering.mall.domain.dto.repository;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SavedMallPreviewDto {
    private String name;
    private String url;
    private String image;

    @QueryProjection
    public SavedMallPreviewDto(String name, String url, String image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }
}
