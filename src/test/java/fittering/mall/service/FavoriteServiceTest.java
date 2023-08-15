package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.ResponseMallDto;
import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.dto.service.SignUpDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import fittering.mall.domain.RestPage;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.entity.*;
import org.springframework.data.redis.core.RedisTemplate;

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
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void End() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
    }

    @Test
    @DisplayName("유저 즐겨찾기 쇼핑몰 테스트")
    void favoriteMallTest() {
        User user = userService.save(new SignUpDto("tes", "password", "test@test.com", "M", 1, 2, 3));
        Mall mall1 = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        Mall mall2 = mallService.save(new MallDto(2L, "testMall2", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        Mall mall3 = mallService.save(new MallDto(3L, "testMall3", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));

        Favorite savedFavorite1 = favoriteService.saveFavoriteMall(user.getId(), mall1.getId());
        Favorite savedFavorite2 = favoriteService.saveFavoriteMall(user.getId(), mall2.getId());
        Favorite savedFavorite3 = favoriteService.saveFavoriteMall(user.getId(), mall3.getId());

        List<MallDto> savedFavorite = new ArrayList<>();
        savedFavorite.add(createMallDto(savedFavorite1));
        savedFavorite.add(createMallDto(savedFavorite2));
        savedFavorite.add(createMallDto(savedFavorite3));

        List<ResponseMallDto> findFavorites = favoriteService.userFavoriteMall(user.getId());
        for (int i=0; i<findFavorites.size(); i++) {
            MallDto savedMallDto = savedFavorite.get(i);
            ResponseMallDto findMallDto = findFavorites.get(i);
            checkMallDto(savedMallDto, findMallDto);
        }

        favoriteService.deleteFavoriteMall(user.getId(), mall1.getId());
        favoriteService.deleteFavoriteMall(user.getId(), mall2.getId());
        favoriteService.deleteFavoriteMall(user.getId(), mall3.getId());

        List<ResponseMallDto> deletedUserList = favoriteService.userFavoriteMall(user.getId());
        assertThat(deletedUserList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("유저 좋아요 상품 테스트")
    void favoriteProductTest() {
        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        User user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        List<String> descImgsStr = List.of("descImage.jpg");

        Product product = productService.save(Product.builder()
                                                .price(10000)
                                                .name("tp1")
                                                .gender("M")
                                                .type(0)
                                                .image("image.jpg")
                                                .view(0)
                                                .timeView(0)
                                                .origin("https://test.com/product/1")
                                                .category(category)
                                                .subCategory(subCategory)
                                                .mall(mall)
                                                .build());
        Product product2 = productService.save(Product.builder()
                                                .price(10000)
                                                .name("tp2")
                                                .gender("M")
                                                .type(0)
                                                .image("image.jpg")
                                                .view(0)
                                                .timeView(0)
                                                .origin("https://test.com/product/2")
                                                .category(category)
                                                .subCategory(subCategory)
                                                .mall(mall)
                                                .build());
        Product product3 = productService.save(Product.builder()
                                                .price(10000)
                                                .name("tp3")
                                                .gender("M")
                                                .type(0)
                                                .image("image.jpg")
                                                .view(0)
                                                .timeView(0)
                                                .origin("https://test.com/product/3")
                                                .category(category)
                                                .subCategory(subCategory)
                                                .mall(mall)
                                                .build());

        List<ProductDescription> descImgs = List.of(new ProductDescription(descImgsStr.get(0), product));
        List<ProductDescription> descImgs2 = List.of(new ProductDescription(descImgsStr.get(0), product2));
        List<ProductDescription> descImgs3 = List.of(new ProductDescription(descImgsStr.get(0), product3));

        favoriteService.saveFavoriteProduct(user.getId(), product.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product2.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product3.getId());

        RestPage<ResponseProductPreviewDto> products = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 10));
        assertThat(products.getTotalElements()).isEqualTo(3);
        compareProduct(product, products.getContent().get(0));
        compareProduct(product2, products.getContent().get(1));
        compareProduct(product3, products.getContent().get(2));

        favoriteService.deleteFavoriteProduct(user.getId(), product.getId());
        favoriteService.deleteFavoriteProduct(user.getId(), product2.getId());
        favoriteService.deleteFavoriteProduct(user.getId(), product3.getId());

        RestPage<ResponseProductPreviewDto> deletedProducts = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 10));
        assertThat(deletedProducts.getTotalElements()).isEqualTo(0);
    }

    private static void checkMallDto(MallDto savedMallDto, ResponseMallDto findMallDto) {
        assertThat(savedMallDto.getId()).isEqualTo(findMallDto.getId());
        assertThat(savedMallDto.getName()).isEqualTo(findMallDto.getName());
        assertThat(savedMallDto.getImage()).isEqualTo(findMallDto.getImage());
        assertThat(savedMallDto.getView()).isEqualTo(findMallDto.getView());
    }

    private static MallDto createMallDto(Favorite favorite) {
        Mall mall = favorite.getMall();
        return new MallDto(mall.getId(), mall.getName(), mall.getUrl(), mall.getImage(),
                mall.getDescription(), 0, new ArrayList<>());
    }

    private static void compareProduct(Product savedProduct, ResponseProductPreviewDto productPreviewDto) {
        assertThat(productPreviewDto.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productPreviewDto.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productPreviewDto.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(productPreviewDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productPreviewDto.getMallName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(productPreviewDto.getMallUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }
}