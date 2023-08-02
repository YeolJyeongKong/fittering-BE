package yeolJyeongKong.mall.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import yeolJyeongKong.mall.domain.RestPage;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.ProductDetailDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class FavoriteServiceTest {

    @Autowired
    FavoriteService favoriteService;
    @Autowired
    UserService userService;
    @Autowired
    MallService mallService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;

    @Test
    @DisplayName("유저 즐겨찾기 쇼핑몰 테스트")
    void favoriteMallTest() {
        User user = userService.save(new SignUpDto("tes", "password", "test@test.com", "M", 1, 2, 3));
        Mall mall1 = mallService.save(new MallDto(1L, "testMall1", "image.jpg", 0, new ArrayList<>()));
        Mall mall2 = mallService.save(new MallDto(2L, "testMall2", "image.jpg", 0, new ArrayList<>()));
        Mall mall3 = mallService.save(new MallDto(3L, "testMall3", "image.jpg", 0, new ArrayList<>()));

        Favorite savedFavorite1 = favoriteService.saveFavoriteMall(user.getId(), mall1.getId());
        Favorite savedFavorite2 = favoriteService.saveFavoriteMall(user.getId(), mall2.getId());
        Favorite savedFavorite3 = favoriteService.saveFavoriteMall(user.getId(), mall3.getId());

        List<MallDto> savedFavorite = new ArrayList<>();
        savedFavorite.add(createMallDto(savedFavorite1));
        savedFavorite.add(createMallDto(savedFavorite2));
        savedFavorite.add(createMallDto(savedFavorite3));

        List<MallDto> findFavorites = favoriteService.userFavoriteMall(user.getId());
        for (int i=0; i<findFavorites.size(); i++) {
            MallDto savedMallDto = savedFavorite.get(i);
            MallDto findMallDto = findFavorites.get(i);
            checkMallDto(savedMallDto, findMallDto);
        }

        favoriteService.deleteFavoriteMall(user.getId(), mall1.getId());
        favoriteService.deleteFavoriteMall(user.getId(), mall2.getId());
        favoriteService.deleteFavoriteMall(user.getId(), mall3.getId());

        List<MallDto> deletedUserList = favoriteService.userFavoriteMall(user.getId());
        assertThat(deletedUserList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("유저 좋아요 상품 테스트")
    void favoriteProductTest() {
        Category category = categoryService.save("top");
        Mall mall = mallService.save(new MallDto(1L, "testMall", "image.jpg", 0, new ArrayList<>()));
        User user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};

        Product product = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "top", "testMall",
                        null, null, null, null, descImgsStr),
                category, mall, descImgs));
        Product product2 = productService.save(new Product(
                new ProductDetailDto(10000, "tp2", "M", 0,
                        "image.jpg", "top", "testMall",
                        null, null, null, null, descImgsStr),
                category, mall, descImgs));
        Product product3 = productService.save(new Product(
                new ProductDetailDto(10000, "tp3", "M", 0,
                        "image.jpg", "top", "testMall",
                        null, null, null, null, descImgsStr),
                category, mall, descImgs));

        favoriteService.saveFavoriteProduct(user.getId(), product.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product2.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product3.getId());

        RestPage<ProductPreviewDto> products = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 10));
        assertThat(products.getTotalElements()).isEqualTo(3);
        compareProduct(product, products.getContent().get(0));
        compareProduct(product2, products.getContent().get(1));
        compareProduct(product3, products.getContent().get(2));

        favoriteService.deleteFavoriteProduct(user.getId(), product.getId());
        favoriteService.deleteFavoriteProduct(user.getId(), product2.getId());
        favoriteService.deleteFavoriteProduct(user.getId(), product3.getId());

        RestPage<ProductPreviewDto> deletedProducts = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 10));
        assertThat(deletedProducts.getTotalElements()).isEqualTo(0);
    }

    private static void checkMallDto(MallDto savedMallDto, MallDto findMallDto) {
        assertThat(savedMallDto.getId()).isEqualTo(findMallDto.getId());
        assertThat(savedMallDto.getName()).isEqualTo(findMallDto.getName());
        assertThat(savedMallDto.getImage()).isEqualTo(findMallDto.getImage());
        assertThat(savedMallDto.getView()).isEqualTo(findMallDto.getView());
    }

    private static MallDto createMallDto(Favorite favorite) {
        Mall mall = favorite.getMall();
        return new MallDto(mall.getId(), mall.getName(), mall.getImage(), 0, new ArrayList<>());
    }

    private static void compareProduct(Product savedProduct, ProductPreviewDto productPreviewDto) {
        assertThat(productPreviewDto.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productPreviewDto.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productPreviewDto.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(productPreviewDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productPreviewDto.getMallName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(productPreviewDto.getMallUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }
}