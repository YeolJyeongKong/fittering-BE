package fittering.mall.domain.dto.controller.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSmartMeasurementDto {
    private String frontImage;
    private String sideImage;
    private Integer height;
    private Integer weight;
    private String gender;
}
