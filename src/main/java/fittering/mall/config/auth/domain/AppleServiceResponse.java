package fittering.mall.config.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AppleServiceResponse {
    private String state;
    private String code;
    private String id_token;
    private String user;
}
