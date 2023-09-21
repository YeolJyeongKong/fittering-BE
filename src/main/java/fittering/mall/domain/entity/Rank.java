package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "`rank`")
@NoArgsConstructor(access = PROTECTED)
public class Rank extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "rank_id")
    private Long id;

    @NonNull
    private Integer view;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @Builder
    public Rank(User user, Mall mall, Integer view) {
        this.user = user;
        this.mall = mall;
        this.view = view;
    }

    public void updateView(Integer view) {
        this.view += view;
    }
}
