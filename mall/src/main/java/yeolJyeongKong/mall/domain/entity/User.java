package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import yeolJyeongKong.mall.domain.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    private String email;

    @NonNull
    private String gender;

    @NonNull
    private Integer year;

    @NonNull
    private Integer month;

    @NonNull
    private Integer day;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "measurement_id")
    private Measurement measurement;

    @OneToMany(mappedBy = "user")
    private List<Mall> malls = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private Rank rank;

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private Recent recent;

    public void update(UserDto userDto) {
        username = userDto.getUsername();
        gender = userDto.getGender();
        year = userDto.getYear();
        month = userDto.getMonth();
        day = userDto.getDay();
    }
}
