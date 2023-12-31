package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    @NonNull
    private Integer price;

    @NonNull @Length(max = 50)
    private String name;

    @NonNull @Length(max = 1)
    private String gender;

    /**
     * 0 : 아우터(Outer)
     * 1 : 상의(Top)
     * 2 : 원피스(Dress)
     * 3 : 하의(Bottom)
     */
    @NonNull
    private Integer type;

    @Transient public static final int OUTER = 0;
    @Transient public static final int TOP = 1;
    @Transient public static final int DRESS = 2;
    @Transient public static final int BOTTOM = 3;

    @NonNull
    private String image;

    @NonNull
    private String origin;

    @NonNull
    private Integer view;

    @NonNull
    private Integer timeView;

    @NonNull
    private Integer disabled;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<Size> sizes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<Recent> recents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<Favorite> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<UserRecommendation> userRecommendations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<RecentRecommendation> recentRecommendations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<ProductDescription> productDescriptions = new ArrayList<>();

    protected Product() {
    }

    public void updateView(Integer view) {
        this.view += view;
    }

    public void updateTimeView(Integer timeView) {
        this.timeView += timeView;
    }

    public void initializeTimeView() {
        timeView = 0;
    }

    public void updateInfo(Integer price, Integer disabled, LocalDateTime updated_at) {
        this.price = price;
        this.disabled = disabled;
        super.updateTime(updated_at);
    }
}
