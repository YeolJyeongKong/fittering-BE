package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Top {

    @Id @GeneratedValue
    @Column(name = "top_id")
    private Long id;

    @NonNull
    private Integer full;

    private Integer shoulder;

    @NonNull
    private Integer chest;

    @NonNull
    private Integer sleeve;

    @OneToOne(mappedBy = "top", fetch = LAZY)
    private Size size;
}
