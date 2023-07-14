package yeolJyeongKong.mall.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MeasurementDto {
    private Integer height;
    private Integer weight;
    private Integer arm;
    private Integer leg;
    private Integer shoulder;
    private Integer waist;
    private Integer chest;
    private Integer thigh;
    private Integer hip;

    @QueryProjection
    public MeasurementDto(Integer height, Integer weight, Integer arm, Integer leg, Integer shoulder,
                          Integer waist, Integer chest, Integer thigh, Integer hip) {
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
