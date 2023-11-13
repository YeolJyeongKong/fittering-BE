package fittering.mall.service;

import fittering.mall.controller.dto.response.ResponseMallPreviewDto;
import fittering.mall.controller.dto.response.ResponseMallWithProductDto;
import fittering.mall.repository.RankRepository;
import fittering.mall.service.dto.MallDto;
import fittering.mall.service.dto.SignUpDto;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class RankServiceTest {

    @Autowired
    RankService rankService;

    @Autowired
    MallService mallService;

    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @Autowired
    RankRepository rankRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        rankRepository.deleteAllInBatch();
    }

    @DisplayName("랭킹을 저장한다.")
    @Test
    void saveRank() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));

        //when
        Rank savedRank = rankService.save(user.getId(), mall.getId());

        //then
        assertThat(savedRank.getId()).isNotNull();
        assertThat(savedRank)
                .extracting("user.id", "mall.id")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        mall.getId()
                );
    }

    @DisplayName("유효하지 않은 유저 정보일 경우 랭킹을 저장을 저장할 수 없다.")
    @Test
    void saveRankWhenNoUser() {
        //given
        Long fakeUserId = 1L;
        Mall mall = mallService.save(createMall(1));

        //when //then
        assertThatThrownBy(() -> rankService.save(fakeUserId, mall.getId()))
                .isInstanceOf(NoResultException.class)
                .hasMessage("user doesn't exist");
    }

    @DisplayName("유효하지 않은 쇼핑몰 정보일 경우 랭킹을 저장을 저장할 수 없다.")
    @Test
    void saveRankWhenNoMall() {
        //given
        Long fakeMallId = 1L;
        User user = userService.save(createUser());

        //when //then
        assertThatThrownBy(() -> rankService.save(user.getId(), fakeMallId))
                .isInstanceOf(NoResultException.class)
                .hasMessage("mall doesn't exist");
    }

    @DisplayName("랭킹을 id로 조회할 수 있다.")
    @Test
    void findRankById() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Rank rank = rankService.save(user.getId(), mall.getId());

        //when
        Rank target = rankService.findById(rank.getId());

        //then
        assertThat(target)
                .extracting("user.id", "mall.id")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        mall.getId()
                );
    }

    @DisplayName("특정 사용자의 쇼핑몰 랭킹을 조회할 수 있다.")
    @Test
    void getMallRank() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Mall mall2 = mallService.save(createMall(2));

        rankService.save(user.getId(), mall.getId());
        rankService.save(user.getId(), mall2.getId());

        //when
        List<ResponseMallWithProductDto> target = rankService.mallRank(user.getId());

        //then
        assertThat(target)
                .extracting("id", "name", "image", "description", "view")
                .containsExactlyInAnyOrder(
                        tuple(mall.getId(), mall.getName(), mall.getImage(), mall.getDescription(), 0),
                        tuple(mall2.getId(), mall2.getName(), mall2.getImage(), mall2.getDescription(), 0)
                );
    }

    @DisplayName("특정 사용자의 쇼핑몰 랭킹을 조회할 수 있다.")
    @Test
    void getMallRankPreview() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Mall mall2 = mallService.save(createMall(2));

        rankService.save(user.getId(), mall.getId());
        rankService.save(user.getId(), mall2.getId());

        //when
        List<ResponseMallPreviewDto> target = rankService.mallRankPreview(user.getId(), PageRequest.of(0, 20), 2);

        //then
        assertThat(target)
                .extracting("id", "name", "image")
                .containsExactlyInAnyOrder(
                        tuple(mall.getId(), mall.getName(), mall.getImage()),
                        tuple(mall2.getId(), mall2.getName(), mall2.getImage())
                );
    }

    @DisplayName("쇼핑몰 조회 시 랭킹 조회수로 반영한다.")
    @Test
    void updateViewOnMall() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Rank rank = rankService.save(user.getId(), mall.getId());

        //when
        rankService.updateViewOnMall(user.getId(), mall.getId());
        redisService.batchUpdateView();

        //then
        rankRepository.findById(rank.getId()).ifPresent(target -> {
            assertThat(target)
                    .extracting("view", "user.id", "mall.id")
                    .containsExactlyInAnyOrder(
                            1, user.getId(), mall.getId()
                    );
        });
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
}