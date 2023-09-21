package fittering.mall.controller.dto.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMallDto {
    @NonNull @Length(max = 30)
    private String name;
    @NonNull
    private String url;
    private String image;
    @NonNull
    private String description;
    private Integer view;
    private List<RequestMallRankProductDto> products;
}
