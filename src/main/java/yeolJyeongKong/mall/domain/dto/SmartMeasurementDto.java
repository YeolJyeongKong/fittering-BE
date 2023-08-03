package yeolJyeongKong.mall.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmartMeasurementDto {
    private String frontImage;
    private String sideImage;
    private Integer height;
    private Integer weight;
    private String gender;
}
