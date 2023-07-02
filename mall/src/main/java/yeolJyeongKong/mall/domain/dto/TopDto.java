package yeolJyeongKong.mall.domain.dto;

import lombok.Getter;
import yeolJyeongKong.mall.domain.entity.Size;

@Getter
public class TopDto {
    private String name;
    private Integer full;
    private Integer shoulder;
    private Integer chest;
    private Integer sleeve;

    public TopDto(Size size) {
        name = size.getName();
        full = size.getTop().getFull();
        shoulder = size.getTop().getShoulder();
        chest = size.getTop().getChest();
        sleeve = size.getTop().getSleeve();
    }
}
