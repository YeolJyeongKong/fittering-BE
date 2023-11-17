package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fittering.mall.config.auth.domain.Oauth2UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import fittering.mall.service.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails, OAuth2User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NonNull @Length(max = 20)
    private String username;

    @NonNull
    private String password;

    @Email
    private String email;

    @NonNull @Length(max = 1)
    private String gender;

    @NonNull
    private Integer year;

    @NonNull
    private Integer month;

    @NonNull
    private Integer day;

    private LocalDateTime recentLastInitializedAt;
    private LocalDateTime passwordToken;

    /**
     * 0 : 0~18세
     * 1 : 19~23세
     * 2 : 24~28세
     * 3 : 29~33세
     * 4 : 34~39세
     * 5 : 40세~
     */
    @NonNull
    private Integer ageRange;

    @NonNull
    @Builder.Default
    @ElementCollection(fetch = EAGER)
    private List<String> roles = new ArrayList<>();

    private String provider;
    private String providerId;
    private String providerLoginId; //{provider}_{providerId}

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Rank> ranks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<Recent> recents = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<UserRecommendation> userRecommendations = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<RecentRecommendation> recentRecommendations = new ArrayList<>();

    @Transient
    private Oauth2UserInfo oAuth2UserInfo;

    public User() {
    }

    public User(Oauth2UserInfo oAuth2UserInfo) {
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public void update(UserDto userDto) {
        username = userDto.getUsername();
        gender = userDto.getGender();
        year = userDto.getYear();
        month = userDto.getMonth();
        day = userDto.getDay();
        ageRange = getAgeRange(year, month, day);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean updatePasswordToken(LocalDateTime updatedTime) {
        if(passwordToken == null) {
            passwordToken = updatedTime;
            return true;
        }

        if(Duration.between(passwordToken, updatedTime).getSeconds() < 3600) {
            return false;
        }

        passwordToken = updatedTime;
        return true;
    }

    public void updateRecentLastInitializedAt(LocalDateTime updatedTime) {
        recentLastInitializedAt = updatedTime;
    }

    public static Integer getAgeRange(Integer year, Integer month, Integer day) {
        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();

        int yearDiff = currentDate.getYear() - year;
        int age = birthDate.isBefore(currentDate) ? yearDiff - 1 : yearDiff;

        if (age <= 18) return 0;
        if (age <= 23) return 1;
        if (age <= 28) return 2;
        if (age <= 33) return 3;
        if (age <= 39) return 4;
        return 5;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
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
