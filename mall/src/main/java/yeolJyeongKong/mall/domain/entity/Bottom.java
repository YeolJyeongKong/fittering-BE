package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Bottom {

    @Id @GeneratedValue
    @Column(name = "bottom_id")
    private Long id;

    @NonNull
    private Integer full;

    @NonNull
    private Integer waist;

    @NonNull
    private Integer thigh;

    @NonNull
    private Integer rise;

    @NonNull
    private Integer bottomWidth;

    private Integer hipWidth;

    @OneToOne(mappedBy = "bottom", fetch = LAZY)
    private Size size;
}
