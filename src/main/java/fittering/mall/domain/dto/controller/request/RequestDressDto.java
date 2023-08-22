package fittering.mall.domain.dto.controller.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDressDto {
    private String name;
    @NonNull
    private Double full;
    private Double shoulder;
    @NonNull
    private Double chest;
    private Double waist;
    private Double armHall;
    private Double hip;
    private Double sleeve;
    private Double sleeveWidth;
    private Double bottomWidth;
}