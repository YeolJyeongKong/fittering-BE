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

    @NonNull @Length(max = 4)
    private String name;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "top_id")
    private Top top;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "bottom_id")
    private Bottom bottom;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public Size(TopDto topDto) {
        this.name = topDto.getName();
        this.top = new Top(topDto);
    }

    public Size(BottomDto bottomDto) {
        this.name = bottomDto.getName();
        this.bottom = new Bottom(bottomDto);
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
