package fittering.mall.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMallWithProductDto {
    private Long id;
    private String name;
    private String image;
    private String description;
    private Integer view;
    private Boolean isFavorite;
    private List<ResponseMallRankProductDto> products;
}
