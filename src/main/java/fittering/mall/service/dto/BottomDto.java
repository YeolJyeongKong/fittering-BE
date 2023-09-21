package fittering.mall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BottomDto {
    private String name;
    private Double full;
    private Double waist;
    private Double thigh;
    private Double rise;
    private Double bottomWidth;
    private Double hipWidth;
}
