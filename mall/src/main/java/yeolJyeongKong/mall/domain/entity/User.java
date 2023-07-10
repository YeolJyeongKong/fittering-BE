package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.dto.UserDto;

import java.time.LocalDate;
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

    @NonNull @Length(max = 10)
    private String username;

    @NonNull @Length(min = 8, max = 15)
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

    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites = new ArrayList<>();

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

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    @OneToMany(mappedBy = "user")
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private Rank rank;

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private Recent recent;

    public User(SignUpDto signUpDto, String password) {
        email = signUpDto.getEmail();
        username = signUpDto.getUsername();
        gender = signUpDto.getGender();
        year = signUpDto.getYear();
        month = signUpDto.getMonth();
        day = signUpDto.getDay();
        ageRange = getAgeRange(year, month, day);
        this.password = password;
        roles.add("USER");
    }

    public void update(UserDto userDto) {
        username = userDto.getUsername();
        gender = userDto.getGender();
        year = userDto.getYear();
        month = userDto.getMonth();
        day = userDto.getDay();
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
}
