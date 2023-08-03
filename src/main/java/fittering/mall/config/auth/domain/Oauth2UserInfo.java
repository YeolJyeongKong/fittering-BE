package fittering.mall.config.auth.domain;

import java.util.Map;

public interface Oauth2UserInfo {
    public Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
