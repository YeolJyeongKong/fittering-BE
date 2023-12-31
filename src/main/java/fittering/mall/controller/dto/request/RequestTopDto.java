package fittering.mall.controller.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTopDto {
    private String name;
    @NonNull
    private Double full;
    private Double shoulder;
    @NonNull
    private Double chest;
    @NonNull
    private Double sleeve;
}
