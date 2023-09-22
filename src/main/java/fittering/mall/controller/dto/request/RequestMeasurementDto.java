package fittering.mall.controller.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMeasurementDto {
    @NonNull
    private Double height;
    @NonNull
    private Double weight;
    private Double arm;
    private Double leg;
    private Double shoulder;
    private Double waist;
    private Double chest;
    private Double thigh;
    private Double hip;
}
