package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class Size {

    @Id @GeneratedValue
    @Column(name = "size_id")
    private Long id;

    @NonNull
    private String name;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "top_id")
    private Top top;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "bottom_id")
    private Bottom bottom;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
