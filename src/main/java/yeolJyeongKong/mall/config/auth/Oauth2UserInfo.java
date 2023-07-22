package yeolJyeongKong.mall.config.auth;

import java.util.Map;

public interface Oauth2UserInfo {
    public Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
