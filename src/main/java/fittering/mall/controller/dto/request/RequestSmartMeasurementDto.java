package fittering.mall.controller.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSmartMeasurementDto {
    @NonNull
    private String front;
    @NonNull
    private String side;
    @NonNull
    private Double height;
    @NonNull
    private Double weight;
    @NonNull
    private String sex;
}
