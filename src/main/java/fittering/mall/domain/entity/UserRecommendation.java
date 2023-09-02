package fittering.mall.domain.entity;

import fittering.mall.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserRecommendation extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "user_recommendation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public UserRecommendation(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}
