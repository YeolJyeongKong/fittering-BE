package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Product;
import fittering.mall.domain.entity.RecentRecommendation;
import fittering.mall.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class RecentRecommendationRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private RecentRecommendationRepository recentRecommendationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("유저 id로 최근 본 상품 기준 추천 상품을 조회한다.")
    @Test
    void findRecentRecommendationByUserId() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());

        RecentRecommendation recentRecommendation = recentRecommendationRepository.save(RecentRecommendation.builder()
                .user(user)
                .product(product)
                .build());

        //when
        List<RecentRecommendation> target = recentRecommendationRepository.findByUserId(user.getId());

        //then
        assertThat(target).hasSize(1)
                .extracting("id", "user.id", "product.id")
                .containsExactlyInAnyOrder(
                        tuple(recentRecommendation.getId(), user.getId(), product.getId())
                );
    }

    @DisplayName("유저 id로 최근 본 상품 기준 추천 상품을 삭제한다.")
    @Test
    void deleteRecentRecommendationByUserId() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());

        recentRecommendationRepository.save(RecentRecommendation.builder()
                .user(user)
                .product(product)
                .build());

        //when
        recentRecommendationRepository.deleteByUserId(user.getId());

        //then
        List<RecentRecommendation> target = recentRecommendationRepository.findByUserId(user.getId());
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