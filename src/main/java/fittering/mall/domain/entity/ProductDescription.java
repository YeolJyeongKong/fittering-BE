package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fittering.mall.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ProductDescription extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "description_images_id")
    private Long id;

    @NonNull
    private String url;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ProductDescription(String url, Product product) {
        this.url = url;
        this.product = product;
    }
}
