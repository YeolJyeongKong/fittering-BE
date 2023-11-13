package fittering.mall.service;

import fittering.mall.controller.dto.response.ResponseMallWithProductDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.repository.FavoriteRepository;
import fittering.mall.repository.MallRepository;
import fittering.mall.repository.ProductRepository;
import fittering.mall.repository.UserRepository;
import fittering.mall.service.dto.SignUpDto;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import fittering.mall.domain.RestPage;
import fittering.mall.service.dto.MallDto;
import fittering.mall.domain.entity.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
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
    UserRepository userRepository;

    @Autowired
    MallRepository mallRepository;

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        favoriteRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("즐겨찾는 쇼핑몰 정보를 저장할 수 있다.")
    @Test
    void saveFavoriteMall() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));

        //when
        Favorite savedFavorite = favoriteService.saveFavoriteMall(user.getId(), mall.getId());

        //then
        assertThat(savedFavorite.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedFavorite.getMall().getId()).isEqualTo(mall.getId());
    }

    @DisplayName("올바르지 않은 유저 정보라면 즐겨찾기 기능을 이용할 수 없다.")
    @Test
    void saveFavoriteMallWithInvalidUser() {
        //given
        Long mockUserId = 1L;
        Mall mall = mallService.save(createMall(1));

        //when //then
        assertThatThrownBy(() -> favoriteService.saveFavoriteMall(mockUserId, mall.getId()))
                .isInstanceOf(NoResultException.class)
                .hasMessage("user doesn't exist");
    }

    @DisplayName("올바르지 않은 쇼핑몰 정보라면 즐겨찾기 기능을 이용할 수 없다.")
    @Test
    void saveFavoriteMallWithInvalidMall() {
        //given
        User user = userService.save(createUser());
        Long mallId = 1L;

        //when //then
        assertThatThrownBy(() -> favoriteService.saveFavoriteMall(user.getId(), mallId))
                .isInstanceOf(NoResultException.class)
                .hasMessage("mall doesn't exist");
    }

    @DisplayName("유저의 즐겨찾기 쇼핑몰을 삭제할 수 있다.")
    @Test
    void deleteFavoriteMallOfUser() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));

        favoriteService.saveFavoriteMall(user.getId(), mall.getId());

        //when
        favoriteService.deleteFavoriteMall(user.getId(), mall.getId());

        //then
        List<ResponseMallWithProductDto> responseFavoriteMalls = favoriteService.userFavoriteMall(user.getId());
        assertThat(responseFavoriteMalls).isEmpty();
    }

    @DisplayName("유저의 즐겨찾기 쇼핑몰 리스트를 조회할 수 있다.")
    @Test
    void getFavoriteMallListOfUser() {
        //given
        User user = userService.save(createUser());
        Mall mall1 = mallService.save(createMall(1));
        Mall mall2 = mallService.save(createMall(2));
        Mall mall3 = mallService.save(createMall(3));

        favoriteService.saveFavoriteMall(user.getId(), mall1.getId());
        favoriteService.saveFavoriteMall(user.getId(), mall2.getId());
        favoriteService.saveFavoriteMall(user.getId(), mall3.getId());

        //when
        List<ResponseMallWithProductDto> responseFavoriteMalls = favoriteService.userFavoriteMall(user.getId());

        //then
        assertThat(responseFavoriteMalls).hasSize(3)
                .extracting("name", "image", "description", "view", "isFavorite")
                .containsExactlyInAnyOrder(
                        tuple("testMall1", "image.jpg", "desc", 0, true),
                        tuple("testMall2", "image.jpg", "desc", 0, true),
                        tuple("testMall3", "image.jpg", "desc", 0, true)
                );
    }

    @DisplayName("유저의 좋아요한 상품을 저장할 수 있다.")
    @Test
    void saveFavoriteProductOfUser() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        Product product = productService.save(createProduct(1, category, subCategory, mall));

        //when
        Favorite savedProduct = favoriteService.saveFavoriteProduct(user.getId(), product.getId());

        //then
        assertThat(savedProduct.getProduct())
                .extracting("price", "name", "gender", "type", "image", "origin", "disabled")
                .containsExactlyInAnyOrder(
                        10000, "product1", "M", 0, "image.jpg", "https://test.com/product/1", 0
                );
    }

    @DisplayName("유저의 좋아요한 상품을 삭제할 수 있다.")
    @Test
    void deleteFavoriteProductOfUser() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        Product product = productService.save(createProduct(1, category, subCategory, mall));

        favoriteService.saveFavoriteProduct(user.getId(), product.getId());

        //when
        favoriteService.deleteFavoriteProduct(user.getId(), product.getId());

        //then
        List<ResponseProductPreviewDto> responseFavoriteProducts = favoriteService.userFavoriteProductPreview(user.getId());
        assertThat(responseFavoriteProducts).isEmpty();
    }

    @DisplayName("유저가 좋아요한 상품들을 조회할 수 있다.")
    @Test
    void getFavoriteProductsOfUser() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        Product product = productService.save(createProduct(1, category, subCategory, mall));
        Product product2 = productService.save(createProduct(2, category, subCategory, mall));
        Product product3 = productService.save(createProduct(3, category, subCategory, mall));

        favoriteService.saveFavoriteProduct(user.getId(), product.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product2.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product3.getId());

        //when
        RestPage<ResponseProductPreviewDto> responseProducts = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 20));

        //then
        assertThat(responseProducts.getTotalElements()).isEqualTo(3);
        assertThat(responseProducts)
                .extracting("productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple("image.jpg", "product1", 10000, "testMall1", "https://www.test-mall.com"),
                        tuple("image.jpg", "product2", 10000, "testMall1", "https://www.test-mall.com"),
                        tuple("image.jpg", "product3", 10000, "testMall1", "https://www.test-mall.com")
                );
    }

    @DisplayName("미리보기 버전에서 유저가 좋아요한 상품들을 조회할 수 있다.")
    @Test
    void getFavoriteProductsOfUserOnPreview() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        Product product = productService.save(createProduct(1, category, subCategory, mall));
        Product product2 = productService.save(createProduct(2, category, subCategory, mall));
        Product product3 = productService.save(createProduct(3, category, subCategory, mall));

        favoriteService.saveFavoriteProduct(user.getId(), product.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product2.getId());
        favoriteService.saveFavoriteProduct(user.getId(), product3.getId());

        //when
        List<ResponseProductPreviewDto> responseProducts = favoriteService.userFavoriteProductPreview(user.getId());

//        log.info("responseProduct.id={} {} {}", responseProducts.get(0).getProductId(),
//                responseProducts.get(1).getProductId(),
//                responseProducts.get(2).getProductId());

        //then
        assertThat(responseProducts).hasSize(3)
                .extracting("productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple("image.jpg", "product1", 10000, "testMall1", "https://www.test-mall.com"),
                        tuple("image.jpg", "product2", 10000, "testMall1", "https://www.test-mall.com"),
                        tuple("image.jpg", "product3", 10000, "testMall1", "https://www.test-mall.com")
                );
    }

    private SignUpDto createUser() {
        return SignUpDto.builder()
                .username("testuser")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();
    }

    private MallDto createMall(int number) {
        return MallDto.builder()
                .name("testMall" + number)
                .url("https://www.test-mall.com")
                .image("image.jpg")
                .description("desc")
                .view(0)
                .products(new ArrayList<>())
                .build();
    }

    private Product createProduct(int number, Category category, SubCategory subCategory, Mall mall) {
        return Product.builder()
                .price(10000)
                .name("product" + number)
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/" + number)
                .category(category)
                .subCategory(subCategory)
                .mall(mall)
                .disabled(0)
                .build();
    }
}