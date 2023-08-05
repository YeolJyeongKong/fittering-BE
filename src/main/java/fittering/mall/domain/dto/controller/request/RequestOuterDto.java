package fittering.mall.domain.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOuterDto {
    private String name;
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double sleeve;
}
