package fittering.mall.domain.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OuterDto {
    private String name;
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double sleeve;
}
