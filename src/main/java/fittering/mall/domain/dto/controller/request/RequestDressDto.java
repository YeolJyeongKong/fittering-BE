package fittering.mall.domain.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDressDto {
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
}