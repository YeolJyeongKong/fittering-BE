package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeolJyeongKong.mall.domain.entity.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DressDto {
    private String name;
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double waist;
    private Double armHall;
    private Double hip;
    private Double sleeve;
    private Double sleeveWidth;
    private Double bottomWidth;

    public DressDto(Size size) {
        name = size.getName();
        full = size.getDress().getFull();
        shoulder = size.getDress().getShoulder();
        chest = size.getDress().getChest();
        waist = size.getDress().getWaist();
        armHall = size.getDress().getArmHall();
        hip = size.getDress().getHip();
        sleeve = size.getDress().getSleeve();
        sleeveWidth = size.getDress().getSleeveWidth();
        bottomWidth = size.getDress().getBottomWidth();
    }
}
