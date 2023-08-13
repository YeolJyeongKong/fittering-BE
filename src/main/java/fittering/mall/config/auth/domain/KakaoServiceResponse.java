package fittering.mall.config.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KakaoServiceResponse {
    private String token_type;
    private String access_token;
    private String id_token;
    private String scope;
}
