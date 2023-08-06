package fittering.mall.domain.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallDto {
    private Long id;
    private String name;
    private String url;
    private String image;
    private String description;
    private Integer view;
    private List<MallRankProductDto> products;
}
