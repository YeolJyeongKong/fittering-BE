package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class DescriptionImage {

    @Id @GeneratedValue
    @Column(name = "description_images_id")
    private Long id;

    @NonNull
    private String url;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public DescriptionImage(String url) {
        this.url = url;
    }
}
