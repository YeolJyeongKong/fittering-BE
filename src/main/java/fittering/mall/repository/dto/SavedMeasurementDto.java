package fittering.mall.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SavedMeasurementDto {
    private Double height;
    private Double weight;
    private Double arm;
    private Double leg;
    private Double shoulder;
    private Double waist;
    private Double chest;
    private Double thigh;
    private Double hip;

    @QueryProjection
    public SavedMeasurementDto(Double height, Double weight, Double arm, Double leg, Double shoulder,
                               Double waist, Double chest, Double thigh, Double hip) {
        this.height = height;
        this.weight = weight;
        this.arm = arm;
        this.leg = leg;
        this.shoulder = shoulder;
        this.waist = waist;
        this.chest = chest;
        this.thigh = thigh;
        this.hip = hip;
    }
}
