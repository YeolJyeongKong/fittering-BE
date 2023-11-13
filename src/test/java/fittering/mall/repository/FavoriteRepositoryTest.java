package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.domain.entity.Favorite;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Product;
import fittering.mall.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class FavoriteRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MallRepository mallRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("특정 유저 id와 관련된 즐겨찾기 정보를 삭제한다.")
    @Test
    void deleteByUserId() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Favorite savedFavorite = favoriteRepository.save(createFavorite(user, mall));

        //when
        favoriteRepository.deleteByUserId(user.getId());

        //then
        Optional<Favorite> target = favoriteRepository.findById(savedFavorite.getId());
        assertThat(target).isEmpty();
    }

    @DisplayName("유저가 즐겨찾기한 쇼핑몰을 조회할 수 있다.")
    @Test
    void userFavoriteMall() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        favoriteRepository.save(createFavorite(user, mall));

        //when
        List<Favorite> target = favoriteRepository.userFavoriteMall(user.getId());

        //then
        assertThat(target).hasSize(1)
                .extracting("user.id", "mall.id")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), mall.getId())
                );
    }

    @DisplayName("유저가 즐겨찾기한 상품을 조회할 수 있다.")
    @Test
    void userFavoriteProduct() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());
        favoriteRepository.save(createFavorite(user, product));

        //when
        Page<ResponseProductPreviewDto> target = favoriteRepository.userFavoriteProduct(user.getId(), PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice())
                );
    }

    @DisplayName("미리보기 버전에서 유저가 즐겨찾기한 상품을 조회할 수 있다.")
    @Test
    void userFavoriteProductPreview() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());
        favoriteRepository.save(createFavorite(user, product));

        //when
        List<ResponseProductPreviewDto> target = favoriteRepository.userFavoriteProductPreview(user.getId());

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice())
                );
    }

    @DisplayName("유저가 즐겨찾기한 쇼핑몰 정보를 삭제할 수 있다.")
    @Test
    void deleteByUserIdAndMallId() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        favoriteRepository.save(createFavorite(user, mall));

        //when
        favoriteRepository.deleteByUserIdAndMallId(user.getId(), mall.getId());

        //then
        Boolean target = favoriteRepository.isUserFavoriteMall(user.getId(), mall.getId());
        assertThat(target).isFalse();
    }

    @DisplayName("유저가 즐겨찾기한 상품 정보를 삭제할 수 있다.")
    @Test
    void deleteByUserIdAndProductId() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());
        favoriteRepository.save(createFavorite(user, product));

        //when
        favoriteRepository.deleteByUserIdAndProductId(user.getId(), product.getId());

        //then
        Boolean target = favoriteRepository.isUserFavoriteProduct(user.getId(), product.getId());
        assertThat(target).isFalse();
    }

    @DisplayName("유저가 즐겨찾기한 쇼핑몰인지 확인할 수 있다.")
    @Test
    void isUserFavoriteMall() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        favoriteRepository.save(createFavorite(user, mall));

        //when
        Boolean target = favoriteRepository.isUserFavoriteMall(user.getId(), mall.getId());

        //then
        assertThat(target).isTrue();
    }

    @DisplayName("유저가 즐겨찾기한 상품인지 확인할 수 있다.")
    @Test
    void isUserFavoriteProduct() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());
        favoriteRepository.save(createFavorite(user, product));

        //when
        Boolean target = favoriteRepository.isUserFavoriteProduct(user.getId(), product.getId());

        //then
        assertThat(target).isTrue();
    }

    private User createUser() {
        return User.builder()
                .username("testuser")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(0)
                .roles(List.of("ROLE_USER"))
                .build();
    }

    private Mall createMall() {
        return Mall.builder()
                .name("열졍콩몰")
                .url("https://www.test-mall.com")
                .image("image.jpg")
                .description("description")
                .build();
    }

    private Product createProduct() {
        return Product.builder()
                .price(1000)
                .name("product")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .origin("https://test-mall.com")
                .view(0)
                .timeView(0)
                .disabled(0)
                .build();
    }

    private Favorite createFavorite(User user, Mall mall) {
        return Favorite.builder()
                .user(user)
                .mall(mall)
                .build();
    }

    private Favorite createFavorite(User user, Product product) {
        return Favorite.builder()
                .user(user)
                .product(product)
                .build();
    }
}