package fittering.mall.service;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.config.kafka.domain.dto.CrawledMallDto;
import fittering.mall.config.kafka.domain.dto.CrawledProductDto;
import fittering.mall.config.kafka.domain.dto.CrawledSizeDto;
import fittering.mall.controller.dto.response.*;
import fittering.mall.domain.RestPage;
import fittering.mall.repository.*;
import fittering.mall.service.dto.MallDto;
import fittering.mall.service.dto.ProductParamDto;
import fittering.mall.service.dto.SignUpDto;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MallService mallService;

    @Autowired
    private UserService userService;

    @Autowired
    private RankService rankService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MallRepository mallRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private OuterRepository outerRepository;

    @Autowired
    private TopRepository topRepository;

    @Autowired
    private DressRepository dressRepository;

    @Autowired
    private BottomRepository bottomRepository;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private RecentRecommendationRepository recentRecommendationRepository;

    @Autowired
    private UserRecommendationRepository userRecommendationRepository;

    @Autowired
    private ProductDescriptionRepository productDescriptionRepository;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        rankRepository.deleteAllInBatch();
        recentRecommendationRepository.deleteAllInBatch();
        userRecommendationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        sizeRepository.deleteAllInBatch();
        outerRepository.deleteAllInBatch();
        topRepository.deleteAllInBatch();
        dressRepository.deleteAllInBatch();
        bottomRepository.deleteAllInBatch();
        productDescriptionRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
    }

    @DisplayName("상품을 저장할 수 있다.")
    @Test
    void saveProduct() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");

        Product request = createProduct(1, topCategory, topSubCategory, mall);

        //when
        Product savedProduct = productService.save(request);

        //then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct)
                .extracting("price", "name", "gender", "type", "image", "origin")
                .containsExactlyInAnyOrder(
                        request.getPrice(),
                        request.getName(),
                        request.getGender(),
                        request.getType(),
                        request.getImage(),
                        request.getOrigin()
                );
    }

    @DisplayName("상품을 조회할 수 있다.")
    @Test
    void findProductById() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");

        Product savedProduct = productService.save(createProduct(1, topCategory, topSubCategory, mall));

        //when
        Product target = productService.findById(savedProduct.getId());

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("price", "name", "gender", "type", "image", "origin")
                .containsExactlyInAnyOrder(
                        savedProduct.getPrice(),
                        savedProduct.getName(),
                        savedProduct.getGender(),
                        savedProduct.getType(),
                        savedProduct.getImage(),
                        savedProduct.getOrigin()
                );
    }

    @DisplayName("카테고리로 상품을 조회할 수 있다.")
    @Test
    void findProductByCategory() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");

        Product savedProduct = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        ProductParamDto productParamDto = ProductParamDto.builder()
                .categoryId(topCategory.getId())
                .gender(savedProduct.getGender())
                .filterId(0L)
                .build();

        //when
        RestPage<ResponseProductPreviewDto> target = productService.productWithCategory(productParamDto, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice())
                );
    }

    @DisplayName("서브 카테고리로 상품을 조회할 수 있다.")
    @Test
    void findProductBySubCategory() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");

        Product savedProduct = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        ProductParamDto productParamDto = ProductParamDto.builder()
                .subCategoryId(topSubCategory.getId())
                .gender(savedProduct.getGender())
                .filterId(0L)
                .build();

        //when
        RestPage<ResponseProductPreviewDto> target = productService.productWithSubCategory(productParamDto, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice())
                );
    }

    @DisplayName("쇼핑몰 내 카테고리로 상품을 조회할 수 있다.")
    @Test
    void findProductByCategoryOnMall() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");

        Product savedProduct = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        ProductParamDto productParamDto = ProductParamDto.builder()
                .mallId(mall.getId())
                .categoryId(topCategory.getId())
                .gender(savedProduct.getGender())
                .filterId(0L)
                .build();

        //when
        RestPage<ResponseProductPreviewDto> target = productService.productWithCategoryOfMall(productParamDto, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice())
                );
    }

    @DisplayName("쇼핑몰 내 서브 카테고리로 상품을 조회할 수 있다.")
    @Test
    void findProductBySubCategoryOnMall() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");

        Product savedProduct = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        ProductParamDto productParamDto = ProductParamDto.builder()
                .mallId(mall.getId())
                .subCategoryId(topSubCategory.getId())
                .gender(savedProduct.getGender())
                .filterId(0L)
                .build();

        //when
        RestPage<ResponseProductPreviewDto> target = productService.productWithSubCategoryOfMall(productParamDto, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice())
                );
    }

    @DisplayName("카테고리별 속한 상품 개수를 조회할 수 있다.")
    @Test
    void getProductCountWithCategory() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Category bottomCategory = categoryService.save("bottom");
        SubCategory bottomSubCategory = categoryService.saveSubCategory("bottom", "pants");

        productService.save(createProduct(1, topCategory, topSubCategory, mall));
        productService.save(createProduct(2, bottomCategory, bottomSubCategory, mall));
        productService.save(createProduct(3, bottomCategory, bottomSubCategory, mall));

        //when
        ResponseProductAllCategoryDto target = productService.multipleProductCountWithCategory();

        //then
        assertThat(target.getMain()).hasSize(2)
                .extracting("categoryId", "count")
                .containsExactlyInAnyOrder(
                        tuple(topCategory.getId(), 1L),
                        tuple(bottomCategory.getId(), 2L)
                );
        assertThat(target.getSub()).hasSize(2)
                .extracting("categoryId", "count")
                .containsExactlyInAnyOrder(
                        tuple(topSubCategory.getId(), 1L),
                        tuple(bottomSubCategory.getId(), 2L)
                );
    }

    @DisplayName("쇼핑몰 내 카테고리별 속한 상품 개수를 조회할 수 있다.")
    @Test
    void getProductCountWithCategoryOnMall() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Category bottomCategory = categoryService.save("bottom");
        SubCategory bottomSubCategory = categoryService.saveSubCategory("bottom", "pants");

        productService.save(createProduct(1, topCategory, topSubCategory, mall));
        productService.save(createProduct(2, bottomCategory, bottomSubCategory, mall));
        productService.save(createProduct(3, bottomCategory, bottomSubCategory, mall));

        //when
        ResponseProductAllCategoryDto target = productService.productCountWithCategoryOfMall(mall.getId());

        //then
        assertThat(target.getMain()).hasSize(2)
                .extracting("categoryId", "count")
                .containsExactlyInAnyOrder(
                        tuple(topCategory.getId(), 1L),
                        tuple(bottomCategory.getId(), 2L)
                );
        assertThat(target.getSub()).hasSize(2)
                .extracting("categoryId", "count")
                .containsExactlyInAnyOrder(
                        tuple(topSubCategory.getId(), 1L),
                        tuple(bottomSubCategory.getId(), 2L)
                );
    }

    @DisplayName("쇼핑몰 내 카테고리별 속한 상품 개수를 조회할 수 있다.")
    @Test
    void getRecommendProducts() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Category bottomCategory = categoryService.save("bottom");
        SubCategory bottomSubCategory = categoryService.saveSubCategory("bottom", "pants");

        Product savedProduct = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product savedProduct2 = productService.save(createProduct(2, bottomCategory, bottomSubCategory, mall));
        Product savedProduct3 = productService.save(createProduct(3, bottomCategory, bottomSubCategory, mall));
        List<Long> productIds = List.of(savedProduct.getId(), savedProduct2.getId(), savedProduct3.getId());

        //when
        List<ResponseProductPreviewDto> target = productService.recommendProduct(productIds, false);

        //then
        assertThat(target).hasSize(3)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice()),
                        tuple(savedProduct2.getId(), savedProduct2.getImage(), savedProduct2.getName(), savedProduct2.getPrice()),
                        tuple(savedProduct3.getId(), savedProduct3.getImage(), savedProduct3.getName(), savedProduct3.getPrice())
                );
    }

    @DisplayName("아우터 상품을 조회할 수 있다.")
    @Test
    void getOuterProductDetail() {
        //given
        Long userId = 1L;
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        ResponseOuterDto target = productService.outerProductDetail(userId, savedProduct.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        savedProduct.getImage(),
                        savedProduct.getName(),
                        savedProduct.getGender(),
                        savedProduct.getPrice(),
                        savedProduct.getOrigin(),
                        savedProduct.getCategory().getName(),
                        savedProduct.getSubCategory().getName()
                );
    }

    @DisplayName("상의 상품을 상세 조회할 수 있다.")
    @Test
    void getTopProductDetail() {
        //given
        Long userId = 1L;
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        ResponseTopDto target = productService.topProductDetail(userId, savedProduct.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        savedProduct.getImage(),
                        savedProduct.getName(),
                        savedProduct.getGender(),
                        savedProduct.getPrice(),
                        savedProduct.getOrigin(),
                        savedProduct.getCategory().getName(),
                        savedProduct.getSubCategory().getName()
                );
    }

    @DisplayName("원피스 상품을 상세 조회할 수 있다.")
    @Test
    void getDressProductDetail() {
        //given
        Long userId = 1L;
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        ResponseDressDto target = productService.dressProductDetail(userId, savedProduct.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        savedProduct.getImage(),
                        savedProduct.getName(),
                        savedProduct.getGender(),
                        savedProduct.getPrice(),
                        savedProduct.getOrigin(),
                        savedProduct.getCategory().getName(),
                        savedProduct.getSubCategory().getName()
                );
    }

    @DisplayName("하의 상품을 상세 조회할 수 있다.")
    @Test
    void getBottomProductDetail() {
        //given
        Long userId = 1L;
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        ResponseBottomDto target = productService.bottomProductDetail(userId, savedProduct.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        savedProduct.getImage(),
                        savedProduct.getName(),
                        savedProduct.getGender(),
                        savedProduct.getPrice(),
                        savedProduct.getOrigin(),
                        savedProduct.getCategory().getName(),
                        savedProduct.getSubCategory().getName()
                );
    }

    @DisplayName("상품 조회 시 조회수를 업데이트한다.")
    @Test
    void updateView() {
        //given
        Mall mall = mallService.save(createMall(1));
        User user = userService.save(createUser());
        Rank rank = rankService.save(user.getId(), mall.getId());
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        productService.updateView(savedProduct.getId());
        redisService.batchUpdateView();

        //then
        Product target = productRepository.findById(savedProduct.getId()).get();
        assertThat(target)
                .extracting("view", "timeView")
                .containsExactlyInAnyOrder(
                        1, 1
                );
    }

    @DisplayName("최근 본 상품 기반 추천 상품 정보를 저장한다.")
    @Test
    void saveRecentRecommendation() {
        //given
        Mall mall = mallService.save(createMall(1));
        User user = userService.save(createUser());
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        productService.saveRecentRecommendation(user.getId(), savedProduct.getId());

        //then
        recentRecommendationRepository.findById(savedProduct.getId()).ifPresent(target -> {
            assertThat(target.getId()).isNotNull();
            assertThat(target.getUser().getId()).isEqualTo(user.getId());
            assertThat(target.getProduct().getId()).isEqualTo(savedProduct.getId());
        });
    }

    @DisplayName("존재하지 않는 유저에 대해 최근 본 상품 기반 추천 상품 정보를 저장할 수 없다.")
    @Test
    void saveRecentRecommendationWhenNoUser() {
        //given
        Long fakeUserId = 1L;
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when //then
        assertThatThrownBy(() -> productService.saveRecentRecommendation(fakeUserId, savedProduct.getId()))
                .isInstanceOf(NoResultException.class)
                .hasMessageContaining("user doesn't exist");
    }

    @DisplayName("비슷한 체형 기반 추천 상품 정보를 저장한다.")
    @Test
    void saveUserRecommendation() {
        //given
        Mall mall = mallService.save(createMall(1));
        User user = userService.save(createUser());
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        productService.saveUserRecommendation(user.getId(), savedProduct.getId());

        //then
        userRecommendationRepository.findById(savedProduct.getId()).ifPresent(target -> {
            assertThat(target.getId()).isNotNull();
            assertThat(target.getUser().getId()).isEqualTo(user.getId());
            assertThat(target.getProduct().getId()).isEqualTo(savedProduct.getId());
        });
    }

    @DisplayName("존재하지 않는 유저에 대해 비슷한 체형 기반 추천 상품 정보를 저장한다.")
    @Test
    void saveUserRecommendationWhenNoUser() {
        //given
        Long fakeUserId = 1L;
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when //then
        assertThatThrownBy(() -> productService.saveUserRecommendation(fakeUserId, savedProduct.getId()))
                .isInstanceOf(NoResultException.class)
                .hasMessageContaining("user doesn't exist");
    }

    @DisplayName("최근 본 상품 기반 추천 상품 리스트를 조회한다..")
    @Test
    void getProductsOfRecentRecommendation() {
        //given
        Mall mall = mallService.save(createMall(1));
        User user = userService.save(createUser());
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        Product savedProduct2 = productService.save(createProduct(2, outerCategory, outerSubCategory, mall));
        productService.saveRecentRecommendation(user.getId(), savedProduct.getId());
        productService.saveRecentRecommendation(user.getId(), savedProduct2.getId());

        //when
        productService.productWithRecentRecommendation(user.getId());

        //then
        List<RecentRecommendation> target = recentRecommendationRepository.findByUserId(user.getId());
        assertThat(target).hasSize(2)
                        .extracting("user.id", "product.id")
                        .containsExactlyInAnyOrder(
                                tuple(user.getId(), savedProduct.getId()),
                                tuple(user.getId(), savedProduct2.getId())
                        );
    }

    @DisplayName("비슷한 체형 기반 추천 상품 리스트를 조회한다.")
    @Test
    void getProductsOfUserRecommendation() {
        //given
        Mall mall = mallService.save(createMall(1));
        User user = userService.save(createUser());
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        Product savedProduct2 = productService.save(createProduct(2, outerCategory, outerSubCategory, mall));
        productService.saveUserRecommendation(user.getId(), savedProduct.getId());
        productService.saveUserRecommendation(user.getId(), savedProduct2.getId());

        //when
        productService.productWithUserRecommendation(user.getId());

        //then
        List<UserRecommendation> target = userRecommendationRepository.findByUserId(user.getId());
        assertThat(target).hasSize(2)
                .extracting("user.id", "product.id")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), savedProduct.getId()),
                        tuple(user.getId(), savedProduct2.getId())
                );
    }

    @DisplayName("상품의 설명 정보를 저장할 수 있다.")
    @Test
    void saveProductDescriptions() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        List<String> productDescriptions = List.of("설명1", "설명2");
        String cloudfrontUrl = "https://images.fit-tering.com/images/";

        //when
        List<ProductDescription> target = productService.saveProductDescriptions(productDescriptions, savedProduct);

        //then
        assertThat(target).hasSize(2)
                .extracting("url", "product.id")
                .containsExactlyInAnyOrder(
                        tuple(cloudfrontUrl + productDescriptions.get(0), savedProduct.getId()),
                        tuple(cloudfrontUrl + productDescriptions.get(1), savedProduct.getId())
                );
    }

    @DisplayName("상품의 설명 정보를 조회할 수 있다.")
    @Test
    void getProductDescriptions() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        List<String> productDescriptions = List.of("설명1", "설명2");
        String cloudfrontUrl = "https://images.fit-tering.com/images/";

        productService.saveProductDescriptions(productDescriptions, savedProduct);

        //when
        List<ResponseProductDescriptionDto> target = productService.getProductDescriptions(savedProduct.getId());

        //then
        assertThat(target).hasSize(2)
                .extracting("url")
                .containsExactlyInAnyOrder(
                        cloudfrontUrl + productDescriptions.get(0),
                        cloudfrontUrl + productDescriptions.get(1)
                );
    }

    @DisplayName("상품의 실시간 조회수를 초기화할 수 있다.")
    @Test
    void initializeTimeView() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        productService.updateView(savedProduct.getId());
        redisService.batchUpdateView();

        //when
        productService.initializeTimeView();

        //then
        Product target = productRepository.findById(savedProduct.getId()).get();
        assertThat(target.getTimeView()).isZero();
    }

    @DisplayName("상품의 실시간 조회수를 초기화할 수 있다.")
    @Test
    void getProductsOfTimeRank() {
        //given
        String gender = "M";
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        Product savedProduct2 = productService.save(createProduct(2, outerCategory, outerSubCategory, mall));
        productService.updateView(savedProduct.getId());
        redisService.batchUpdateView();

        //when
        List<ResponseProductPreviewDto> target = productService.productsOfTimeRank(gender);

        //then
        assertThat(target).hasSize(2)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactly(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice()),
                        tuple(savedProduct2.getId(), savedProduct2.getImage(), savedProduct2.getName(), savedProduct2.getPrice())
                );
    }

    @DisplayName("상품 최근 업데이트 날짜를 조회할 수 있다.")
    @Test
    void getProductsOfMaxUpdatedAt() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Product savedProduct = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));
        Product savedProduct2 = productService.save(createProduct(2, outerCategory, outerSubCategory, mall));

        //when
        LocalDateTime target = productService.productsOfMaxUpdatedAt();

        //then
        assertThat(target).isNotEqualTo(savedProduct.getUpdatedAt());
        assertThat(target).isEqualTo(savedProduct2.getUpdatedAt());
    }

    @DisplayName("상품 최근 업데이트 날짜를 조회할 수 있다.")
    @Test
    void updateCrawledProducts() {
        //given
        Mall mall = mallService.save(createMall(1));
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");

        String cloudfrontUrl = "https://images.fit-tering.com/images/";
        String thumbnailPath = "thumbnail";
        CrawledProductDto crawledProductDto = CrawledProductDto.builder()
                .product_id(1L)
                .price(10000)
                .type(0)
                .name("product1")
                .gender("M")
                .category_id(outerCategory.getId())
                .sub_category_id(outerSubCategory.getId())
                .url("https://test.com/product/1")
                .mall_id(mall.getId())
                .description_path("description_path")
                .disabled(0)
                .build();
        CrawledMallDto crawledMallDto = CrawledMallDto.builder()
                .name("testMall1")
                .url("https://www.test-mall.com")
                .description("desc")
                .image("image.jpg")
                .build();
        CrawledSizeDto crawledSizeDto = CrawledSizeDto.builder()
                .full(100.0)
                .shoulder(100.0)
                .chest(100.0)
                .sleeve(100.0)
                .waist(100.0)
                .thigh(100.0)
                .rise(100.0)
                .bottom_width(100.0)
                .hip_width(100.0)
                .arm_hall(100.0)
                .hip(100.0)
                .sleeve_width(100.0)
                .name("size")
                .build();
        List<String> imagePaths = new ArrayList<>();
        imagePaths.add(thumbnailPath);

        //when
        productService.updateCrawledProducts(crawledProductDto, crawledMallDto, List.of(crawledSizeDto), imagePaths);

        //then
        Product target = productRepository.findByName(crawledProductDto.getName()).get();
        assertThat(target)
                .extracting("price", "name", "gender", "type", "image", "origin")
                .containsExactlyInAnyOrder(
                        crawledProductDto.getPrice(),
                        crawledProductDto.getName(),
                        crawledProductDto.getGender(),
                        crawledProductDto.getType(),
                        cloudfrontUrl + thumbnailPath,
                        crawledProductDto.getUrl()
                );
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