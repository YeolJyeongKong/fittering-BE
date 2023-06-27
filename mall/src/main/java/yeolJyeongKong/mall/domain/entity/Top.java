package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.NonNull;

@Entity
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

    @OneToOne(mappedBy = "top")
    private Size size;
}
