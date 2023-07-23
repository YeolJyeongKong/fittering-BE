package yeolJyeongKong.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import yeolJyeongKong.mall.domain.dto.ProductDetailDto;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
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
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @OneToMany(mappedBy = "product")
    private List<Size> sizes = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recent_id")
    private Recent recent;

    @OneToMany(mappedBy = "product")
    private List<Favorite> favorites = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_recommendation_id")
    private UserRecommendation userRecommendation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recent_recommendation_id")
    private RecentRecommendation recentRecommendation;

    public Product(ProductDetailDto productDto, Category category, Mall mall) {
        price = productDto.getPrice();
        name = productDto.getName();
        gender = productDto.getGender();
        type = productDto.getType();
        image = productDto.getImage();
        descriptionImage = productDto.getDescriptionImage();
        view = 0;
        timeView = 0;
        this.category = category;
        this.mall = mall;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public void setRecent(Recent recent) {
        this.recent = recent;
    }

    public void deleteRecent() {
        recent = null;
    }

    public void deleteUserRecommendation() {
        userRecommendation = null;
    }

    public void deleteRecentRecommendation() {
        recentRecommendation = null;
    }

    public void updateView() {
        view = view + 1;
    }
}
