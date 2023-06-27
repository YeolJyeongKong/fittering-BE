package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class Rank {

    @Id @GeneratedValue
    @Column(name = "rank_id")
    private Long id;

    @NonNull
    private Long view;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "mall_id")
    private Mall mall;
}
