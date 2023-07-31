package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @NonNull @Length(max = 10)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }
}
