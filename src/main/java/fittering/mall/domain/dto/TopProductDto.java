package fittering.mall.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import fittering.mall.domain.entity.Product;

import java.util.List;

@Getter
@NoArgsConstructor
public class TopProductDto {
    private Long FavoriteCount;
    private String productImage;
    private String productName;
    private String productGender;
    private Integer price;
    private String mallName;
    private String mallUrl;
    private String mallImage;
    private String category;
    private Integer view;
    private String popularGender;
    private Integer popularAgeRange;

    private List<TopDto> sizes;

    public TopProductDto(Long favoriteCount, Product product, String popularGender,
                         Integer popularAgeRange, List<TopDto> sizes) {
        FavoriteCount = favoriteCount;
        productImage = product.getImage();
        productName = product.getName();
        productGender = product.getGender();
        price = product.getPrice();
        mallName = product.getMall().getName();
        mallUrl = product.getMall().getUrl();
        mallImage = product.getMall().getImage();
        category = product.getCategory().getName();
        view = product.getView();
        this.popularGender = popularGender;
        this.popularAgeRange = popularAgeRange;
        this.sizes = sizes;
    }
}
