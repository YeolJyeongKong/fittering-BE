package fittering.mall.domain.entity;

import fittering.mall.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SubCategory extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "sub_category_id")
    private Long id;

    @NonNull
    @Length(max = 10)
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "subCategory")
    private List<Product> products = new ArrayList<>();

    @Builder
    public SubCategory(Category category, String name) {
        this.category = category;
        this.name = name;
    }
}
