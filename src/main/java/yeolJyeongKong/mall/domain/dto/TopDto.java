package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeolJyeongKong.mall.domain.entity.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopDto {
    private String name;
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double sleeve;

    public TopDto(Size size) {
        name = size.getName();
        full = size.getTop().getFull();
        shoulder = size.getTop().getShoulder();
        chest = size.getTop().getChest();
        sleeve = size.getTop().getSleeve();
    }
}
