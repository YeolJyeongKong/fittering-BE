package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Product {

    @Id @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    @NonNull
    private Integer price;

    @NonNull @Length(max = 30)
    private String name;

    @NonNull @Length(max = 1)
    private String gender;

    /**
     * 0 : 상의(Top)
     * 1 : 하의(Bottom)
     * ...
     */
    @NonNull
    private Integer type;

    @NonNull
    private String image;

    @NonNull
    private Integer view;

    @NonNull
    private Integer timeView;

    private String descriptionImage;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @OneToMany(mappedBy = "product")
    private List<Size> sizes = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recent_id")
    private Recent recent;

    public void updateView() {
        view = view + 1;
    }
}
