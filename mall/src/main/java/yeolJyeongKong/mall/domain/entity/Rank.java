package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "`rank`")
@NoArgsConstructor(access = PROTECTED)
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

    public Rank(User user, Mall mall) {
        this.user = user;
        this.mall = mall;
        view = 0L;
    }

    public void updateView() {
        view++;
    }
}
