package fittering.mall.service;

import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;
import fittering.mall.service.dto.MallDto;
import fittering.mall.service.dto.SignUpDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RankService rankService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OuterRepository outerRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MallRepository mallRepository;

    @Autowired
    private RankRepository rankRepository;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        rankRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        outerRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
    }

    @DisplayName("상품의 조회수를 업데이트할 수 있다.")
    @Test
    void updateViewOfProduct() {
        //given
        Long productId = 1L;

        //when
        redisService.updateViewOfProduct(productId);

        //then
        int target = Integer.parseInt(redisTemplate.opsForValue().get("Batch:Product_view_" + productId).toString());
        assertThat(target).isEqualTo(1);
    }

    @DisplayName("상품의 실시간 조회수를 업데이트할 수 있다.")
    @Test
    void updateTimeViewOfProduct() {
        //given
        Long productId = 1L;

        //when
        redisService.updateTimeViewOfProduct(productId);

        //then
        int target = Integer.parseInt(redisTemplate.opsForValue().get("Batch:Product_timeView_" + productId).toString());
        assertThat(target).isEqualTo(1);
    }

    @DisplayName("랭킹에 집계되는 조회수를 업데이트할 수 있다.")
    @Test
    void batchUpdateViewOfRank() {
        //given
        Long rankId = 1L;

        //when
        redisService.batchUpdateViewOfRank(rankId);

        //then
        int target = Integer.parseInt(redisTemplate.opsForValue().get("Batch:Rank_view_" + rankId).toString());
        assertThat(target).isEqualTo(1);
    }

    @DisplayName("랭킹에 집계되는 조회수를 업데이트할 수 있다.")
    @Test
    void batchUpdateView() {
        //given
        User user = userService.save(createUser());
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product product = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        Rank rank = rankService.save(user.getId(), mall.getId());

        redisService.updateViewOfProduct(product.getId());
        redisService.updateTimeViewOfProduct(product.getId());
        redisService.batchUpdateViewOfRank(rank.getId());

        //when
        redisService.batchUpdateView();

        //then
        productRepository.findById(product.getId()).ifPresent(target -> {
            assertThat(target)
                    .extracting("view", "timeView")
                    .containsExactlyInAnyOrder(
                            1, 1
                    );
        });
        rankRepository.findById(rank.getId()).ifPresent(target -> {
            assertThat(target.getView()).isEqualTo(1);
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