package yeolJyeongKong.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RecentRecommendation {

    @Id @GeneratedValue
    @Column(name = "recent_recommendation_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "recentRecommendation")
    private List<Product> products = new ArrayList<>();

    public RecentRecommendation(User user) {
        this.user = user;
    }

    public void update(List<Product> newProducts) {
        products.addAll(newProducts);
    }
}
