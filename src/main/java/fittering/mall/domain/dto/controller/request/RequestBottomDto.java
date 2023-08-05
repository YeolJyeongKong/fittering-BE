package fittering.mall.domain.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBottomDto {
    private String name;
    private Double full;
    private Double waist;
    private Double thigh;
    private Double rise;
    private Double bottomWidth;
    private Double hipWidth;
}
