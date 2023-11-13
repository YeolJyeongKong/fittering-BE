package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMeasurementDto {
    @NotNull(message = "키를 입력해주세요.")
    private Double height;
    @NotNull(message = "몸무게를 입력해주세요.")
    private Double weight;
    private Double arm;
    private Double leg;
    private Double shoulder;
    private Double waist;
    private Double chest;
    private Double thigh;
    private Double hip;
}
