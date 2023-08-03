package yeolJyeongKong.mall.config.auth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import yeolJyeongKong.mall.config.auth.domain.Oauth2UserInfo;
import yeolJyeongKong.mall.domain.entity.User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Oauth2UserInfo oAuth2UserInfo;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, Oauth2UserInfo oAuth2UserInfo) {
        this.user = user;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getProviderId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
