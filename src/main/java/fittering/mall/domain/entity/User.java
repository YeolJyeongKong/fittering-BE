package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import fittering.mall.domain.dto.UserDto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NonNull @Length(max = 20)
    private String username;

    @NonNull
    private String password;

    @NonNull @Length(min = 7, max = 64)
    private String email;

    @NonNull @Length(max = 1)
    private String gender;

    @NonNull
    private Integer year;

    @NonNull
    private Integer month;

    @NonNull
    private Integer day;

    private LocalDateTime recentlastInializedAt;
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

    protected User() {
    }

    public void update(UserDto userDto) {
        username = userDto.getUsername();
        gender = userDto.getGender();
        year = userDto.getYear();
        month = userDto.getMonth();
        day = userDto.getDay();
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean updatePasswordToken() {
        LocalDateTime now = LocalDateTime.now();

        if(passwordToken == null) {
            passwordToken = now;
            return true;
        }

        if(Duration.between(passwordToken, now).getSeconds() < 3600) {
            return false;
        }

        passwordToken = now;
        return true;
    }

    public void updateRecentlastInializedAt() {
        recentlastInializedAt = LocalDateTime.now();
    }
}
