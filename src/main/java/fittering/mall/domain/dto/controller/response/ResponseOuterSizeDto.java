package fittering.mall.domain.dto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOuterSizeDto {
    private String name;
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double sleeve;
}
