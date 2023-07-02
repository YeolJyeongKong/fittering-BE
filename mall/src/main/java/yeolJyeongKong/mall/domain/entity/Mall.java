package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class Mall {

    @Id @GeneratedValue
    @Column(name = "mall_id")
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String url;

    private String image;

    @NonNull
    private String description;

    @OneToMany(mappedBy = "mall")
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "mall", fetch = LAZY)
    private Rank rank;

    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites = new ArrayList<>();
}
