package fittering.mall.controller.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBottomDto {
    private String name;
    @NonNull
    private Double full;
    @NonNull
    private Double waist;
    private Double thigh;
    private Double rise;
    @NonNull
    private Double bottomWidth;
    private Double hipWidth;
}
