package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @NonNull
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}
