package fittering.mall.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMallDto {
    @NotNull(message = "쇼핑몰 이름은 필수입니다.")
    @Length(max = 30, message = "쇼핑몰 이름은 30자 이하여야 합니다.")
    private String name;
    @NotNull(message = "쇼핑몰 링크는 필수입니다.")
    private String url;
    private String image;
    @NotNull(message = "쇼핑몰 설명은 필수입니다.")
    private String description;
    private Integer view;
    private List<RequestMallRankProductDto> products;
}
