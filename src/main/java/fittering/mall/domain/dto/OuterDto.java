package fittering.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import fittering.mall.domain.entity.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OuterDto {
    private String name;
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double sleeve;

    public OuterDto(Size size) {
        name = size.getName();
        full = size.getOuter().getFull();
        shoulder = size.getOuter().getShoulder();
        chest = size.getOuter().getChest();
        sleeve = size.getOuter().getSleeve();
    }
}
