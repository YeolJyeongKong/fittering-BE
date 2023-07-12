package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class UserRecommendation {

    @Id @GeneratedValue
    @Column(name = "user_recommendation_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "userRecommendation")
    private List<Product> products = new ArrayList<>();
}
