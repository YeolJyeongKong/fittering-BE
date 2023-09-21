package fittering.mall.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMallDto {
    private Long id;
    private String name;
    private String url;
    private String image;
    private String description;
    private Boolean isFavorite;
}
