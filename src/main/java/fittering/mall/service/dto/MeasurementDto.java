package fittering.mall.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDto {
    private Double height;
    private Double weight;
    private Double arm;
    private Double leg;
    private Double shoulder;
    private Double waist;
    private Double chest;
    private Double thigh;
    private Double hip;
}
