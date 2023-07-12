package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class RecentRecommendation {

    @Id @GeneratedValue
    @Column(name = "recent_recommendation_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recentRecommendation")
    private List<Product> products = new ArrayList<>();
}
