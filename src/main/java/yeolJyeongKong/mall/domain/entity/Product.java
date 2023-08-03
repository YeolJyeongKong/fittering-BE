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

    @NonNull
    private String image;

    @NonNull
    private Integer view;

    @NonNull
    private Integer timeView;

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

    @OneToMany(mappedBy = "product")
    private List<Size> sizes = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Recent> recents = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<UserRecommendation> userRecommendations = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<RecentRecommendation> recentRecommendations = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<DescriptionImage> descriptionImages = new ArrayList<>();

    public Product(ProductDetailDto productDto, Category category, SubCategory subCategory,
                   Mall mall, List<DescriptionImage> descriptionImages) {
        price = productDto.getPrice();
        name = productDto.getName();
        gender = productDto.getGender();
        type = productDto.getType();
        image = productDto.getImage();
        view = 0;
        timeView = 0;
        this.category = category;
        this.subCategory = subCategory;
        this.mall = mall;
        this.descriptionImages = descriptionImages;
    }

    public void updateView() {
        view = view + 1;
    }
}
