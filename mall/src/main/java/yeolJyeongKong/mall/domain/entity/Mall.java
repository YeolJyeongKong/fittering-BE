package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.NonNull;

import static jakarta.persistence.FetchType.*;

@Entity
public class Mall {

    @Id @GeneratedValue
    @Column(name = "mall_id")
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String url;

    @NonNull
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "mall")
    private Product product;

    @OneToOne(mappedBy = "mall")
    private Rank rank;
}
