package fittering.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserRecommendation {

    @Id @GeneratedValue
    @Column(name = "user_recommendation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime updatedAt;

    public UserRecommendation(User user, Product product) {
        this.user = user;
        this.product = product;
        updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt() {
        updatedAt = LocalDateTime.now();
    }
}
