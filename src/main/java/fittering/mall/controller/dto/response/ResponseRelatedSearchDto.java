package fittering.mall.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRelatedSearchDto {
    private List<ResponseRelatedSearchProductDto> products;
    private List<ResponseRelatedSearchMallDto> malls;
}
