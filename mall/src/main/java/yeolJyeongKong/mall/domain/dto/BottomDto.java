package yeolJyeongKong.mall.domain.dto;

import lombok.Getter;
import yeolJyeongKong.mall.domain.entity.Size;

@Getter
public class BottomDto {
    private String name;
    private Integer full;
    private Integer waist;
    private Integer thigh;
    private Integer rise;
    private Integer bottomWidth;
    private Integer hipWidth;

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
