package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Product;
import fittering.mall.domain.entity.User;
import fittering.mall.domain.entity.UserRecommendation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserRecommendationRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRecommendationRepository userRecommendationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("유저 id로 추천 상품 정보를 조회할 수 있다.")
    @Test
    void findUserRecommendationByUserId() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());

        UserRecommendation savedUserRecommendation = userRecommendationRepository.save(UserRecommendation.builder()
                .user(user)
                .product(product)
                .build());

        //when
        List<UserRecommendation> target = userRecommendationRepository.findByUserId(user.getId());

        //then
        assertThat(target).isNotNull();
        assertThat(target)
                .extracting("id", "user.id", "product.id")
                .containsExactlyInAnyOrder(
                        tuple(savedUserRecommendation.getId(), user.getId(), product.getId())
                );
    }

    @DisplayName("유저 id로 추천 상품 정보를 삭제할 수 있다.")
    @Test
    void deleteUserRecommendationByUserId() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());

        UserRecommendation savedUserRecommendation = userRecommendationRepository.save(UserRecommendation.builder()
                .user(user)
                .product(product)
                .build());

        //when
        userRecommendationRepository.deleteByUserId(user.getId());

        //then
        List<UserRecommendation> target = userRecommendationRepository.findByUserId(user.getId());
        assertThat(target).isEmpty();
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
}