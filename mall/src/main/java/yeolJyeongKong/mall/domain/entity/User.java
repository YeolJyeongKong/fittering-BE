package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.dto.UserDto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
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
    @ElementCollection(fetch = EAGER)
    private List<String> roles = new ArrayList<>();

    private String provider;
    private String providerId;
    private String providerLoginId; //{provider}_{providerId}

    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    @OneToMany(mappedBy = "user")
    private List<Rank> ranks = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private Recent recent;

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private UserRecommendation userRecommendation;

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private RecentRecommendation recentRecommendation;

    public User(SignUpDto signUpDto, String password, Measurement measurement) {
        email = signUpDto.getEmail();
        username = signUpDto.getUsername();
        gender = signUpDto.getGender();
        year = signUpDto.getYear();
        month = signUpDto.getMonth();
        day = signUpDto.getDay();
        ageRange = getAgeRange(year, month, day);
        this.password = password;
        this.measurement = measurement;
        roles.add("USER");
    }

    public User(String email, String provider, String providerId, Measurement measurement) {
        username = email.substring(0, email.indexOf('@'));
        providerLoginId = provider + "_" + providerId;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.measurement = measurement;
        roles.add("USER");
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

    private Integer getAgeRange(Integer year, Integer month, Integer day) {
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
}
