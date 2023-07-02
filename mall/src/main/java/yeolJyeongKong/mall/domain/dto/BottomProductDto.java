package yeolJyeongKong.mall.domain.dto;

import lombok.Getter;
import yeolJyeongKong.mall.domain.entity.Product;

import java.util.List;

@Getter
public class BottomProductDto {
    private Long FavoriteCount;
    private String productImage;
    private String productName;
    private String productGender;
    private Integer price;
    private String mallName;
    private String mallUrl;
    private String mallImage;
    private String category;
    private String popularGender;
    private Integer popularAgeRange;

    private List<BottomDto> sizes;

    public BottomProductDto(Long favoriteCount, Product product, String popularGender,
                            Integer popularAgeRange, List<BottomDto> sizes) {
        FavoriteCount = favoriteCount;
        productImage = product.getImage();
        productName = product.getName();
        productGender = product.getGender();
        price = product.getPrice();
        mallName = product.getMall().getName();
        mallUrl = product.getMall().getUrl();
        mallImage = product.getMall().getImage();
        category = product.getCategory().getName();
        this.popularGender = popularGender;
        this.popularAgeRange = popularAgeRange;
        this.sizes = sizes;
    }
}
