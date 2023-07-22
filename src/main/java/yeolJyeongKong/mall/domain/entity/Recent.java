package yeolJyeongKong.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Recent {

    @Id @GeneratedValue
    @Column(name = "recent_id")
    private Long id;

    @NonNull
    private LocalDateTime timestamp;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "recent")
    private List<Product> products = new ArrayList<>();

    public Recent(User user, Product product) {
        timestamp = LocalDateTime.now();
        this.user = user;
        products.add(product);
    }

    public void update(Product product) {
        product.setRecent(this);
        products.add(product);
    }
}
