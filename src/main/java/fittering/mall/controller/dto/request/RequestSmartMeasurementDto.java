package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSmartMeasurementDto {
    @NotNull(message = "정면 사진은 필수입니다.")
    private String front;
    @NotNull(message = "측면 사진은 필수입니다.")
    private String side;
    @NotNull(message = "키 정보는 필수입니다.")
    private Double height;
    @NotNull(message = "몸무게 정보는 필수입니다.")
    private Double weight;
    @NotNull(message = "성별 정보는 필수입니다.")
    private String sex;
}
