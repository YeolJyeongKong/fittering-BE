package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeolJyeongKong.mall.domain.entity.Size;

@Getter
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

    public BottomDto(Size size) {
        name = size.getName();
        full = size.getBottom().getFull();
        waist = size.getBottom().getWaist();
        thigh = size.getBottom().getThigh();
        rise = size.getBottom().getRise();
        bottomWidth = size.getBottom().getBottomWidth();
        hipWidth = size.getBottom().getHipWidth();
    }
}
