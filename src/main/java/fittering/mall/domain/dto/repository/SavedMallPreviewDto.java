package fittering.mall.domain.dto.repository;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SavedMallPreviewDto {
    private Long id;
    private String name;
    private String image;

    @QueryProjection
    public SavedMallPreviewDto(Long id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
