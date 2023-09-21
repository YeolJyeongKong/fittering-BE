package fittering.mall.controller.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserCheckDto {
    @NonNull
    private String email;
    @NonNull
    private String password;
}
