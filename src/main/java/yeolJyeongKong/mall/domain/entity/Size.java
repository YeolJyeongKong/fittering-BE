package yeolJyeongKong.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import yeolJyeongKong.mall.domain.dto.BottomDto;
import yeolJyeongKong.mall.domain.dto.TopDto;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Size {

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
    @JoinColumn(name = "bottom_id")
    private Bottom bottom;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public Size(String name, Outer outer, Product product) {
        this.name = name;
        this.outer = outer;
        this.product = product;
    }

    public Size(String name, Top top, Product product) {
        this.name = name;
        this.top = top;
        this.product = product;
    }

    public Size(String name, Bottom bottom, Product product) {
        this.name = name;
        this.bottom = bottom;
        this.product = product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
