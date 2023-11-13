package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Product;
import fittering.mall.domain.entity.Recent;
import fittering.mall.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class RecentRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private RecentRepository recentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MallRepository mallRepository;

    @DisplayName("유저 id로 최근 본 상품 정보를 얻을 수 있다.")
    @Test
    void findRecentRepositoryByUserId() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());

        Recent recent = recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build());

        //when
        List<Recent> target = recentRepository.findByUserId(user.getId());

        //then
        assertThat(target).hasSize(1)
                .extracting("id", "user.id", "product.id")
                .containsExactlyInAnyOrder(
                        tuple(recent.getId(), user.getId(), product.getId())
                );
    }

    @DisplayName("미리보기 버전에서 최근 본 상품 정보를 얻을 수 있다.")
    @Test
    void getRecentProductOnPreview() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));

        Recent recent = recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build());

        //when
        List<ResponseProductPreviewDto> target = recentRepository.recentProductPreview(user.getId());

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice(), mall.getName(), mall.getUrl())
                );

    }

    @DisplayName("최근 본 상품 정보를 얻을 수 있다.")
    @Test
    void getRecentProduct() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));

        Recent recent = recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build());

        //when
        Page<ResponseProductPreviewDto> target = recentRepository.recentProduct(user.getId(), PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice(), mall.getName(), mall.getUrl())
                );
    }

    @DisplayName("유저 id 기반으로 최근 본 상품 정보를 초기화 한다.")
    @Test
    void initializeRecentByUserId() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));

        Recent recent = recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build());

        //when
        recentRepository.initializeRecents(user.getId());

        //then
        List<Recent> target = recentRepository.findByUserId(user.getId());
        assertThat(target).isEmpty();
    }

    @DisplayName("유저 id 기반으로 최근 본 상품 id를 조회할 수 한다.")
    @Test
    void getRecentProductIds() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));
        Product product2 = productRepository.save(createProduct(mall));

        recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build());
        recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product2)
                .build());

        //when
        List<Long> target = recentRepository.recentProductIds(user.getId());

        //then
        assertThat(target).hasSize(2)
                .containsExactlyInAnyOrder(
                        product.getId(), product2.getId()
                );
    }

    @DisplayName("최근 본 상품인지 확인할 수 한다.")
    @Test
    void isRecentProduct() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));
        Product product2 = productRepository.save(createProduct(mall));

        recentRepository.save(Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build());

        //when
        Boolean target = recentRepository.isRecentProduct(user.getId(), product.getId());
        Boolean target2 = recentRepository.isRecentProduct(user.getId(), product2.getId());

        //then
        assertThat(target).isTrue();
        assertThat(target2).isFalse();
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

    private Product createProduct(Mall mall) {
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
                .mall(mall)
                .build();
    }
}