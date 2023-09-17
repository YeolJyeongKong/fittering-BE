package fittering.mall.domain.dto.controller.request;

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
    private Integer height;
    @NonNull
    private Integer weight;
    @NonNull
    private String sex;
}
