package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fittering.mall.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Size extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "size_id")
    private Long id;

    @NonNull @Length(max = 10)
    private String name;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "outer_id")
    private Outer outer;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "top_id")
    private Top top;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "dress_id")
    private Dress dress;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "bottom_id")
    private Bottom bottom;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    protected Size() {
    }
}
