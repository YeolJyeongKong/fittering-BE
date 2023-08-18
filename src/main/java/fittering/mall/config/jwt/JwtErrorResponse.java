package fittering.mall.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
}
