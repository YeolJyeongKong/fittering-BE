package fittering.mall.domain.dto.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSilhouetteApiDto {
    private String front;
    private String side;
}
