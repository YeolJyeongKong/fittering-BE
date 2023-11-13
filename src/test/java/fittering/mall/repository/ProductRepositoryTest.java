package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.domain.entity.*;
import fittering.mall.service.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProductRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MallRepository mallRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

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

    @DisplayName("id로 상품을 조회할 수 있다.")
    @Test
    void findById() {
        //given
        Product product = productRepository.save(createProduct());

        //when
        Optional<Product> target = productRepository.findById(product.getId());

        //then
        assertThat(target).isNotEmpty();
        assertThat(target.get())
                .extracting("id", "price", "name", "gender", "type", "image", "origin", "view", "timeView", "disabled")
                .containsExactlyInAnyOrder(
                        product.getId(),
                        product.getPrice(),
                        product.getName(),
                        product.getGender(),
                        product.getType(),
                        product.getImage(),
                        product.getOrigin(),
                        product.getView(),
                        product.getTimeView(),
                        product.getDisabled()
                );
    }

    @DisplayName("이름으로 상품을 조회할 수 있다.")
    @Test
    void findByName() {
        //given
        Product product = productRepository.save(createProduct());

        //when
        Optional<Product> target = productRepository.findByName(product.getName());

        //then
        assertThat(target).isNotEmpty();
        assertThat(target.get())
                .extracting("price", "name", "gender", "type", "image", "origin", "view", "timeView", "disabled")
                .containsExactlyInAnyOrder(
                        product.getPrice(),
                        product.getName(),
                        product.getGender(),
                        product.getType(),
                        product.getImage(),
                        product.getOrigin(),
                        product.getView(),
                        product.getTimeView(),
                        product.getDisabled()
                );
    }

    @DisplayName("id로 쇼핑몰 정보가 같이 있는 상품을 조회할 수 있다.")
    @Test
    void productById() {
        //given
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));

        //when
        ResponseProductPreviewDto target = productRepository.productById(product.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        product.getImage(),
                        product.getName(),
                        product.getPrice(),
                        mall.getName(),
                        mall.getUrl()
                );
    }

    @DisplayName("카테고리별로 상품을 조회할 수 있다.")
    @Test
    void productWithCategory() {
        //given
        Category topCategory = categoryRepository.save(Category.builder()
                .name("top")
                .build());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall, topCategory));

        //when
        Page<ResponseProductPreviewDto> target = productRepository.productWithCategory(mall.getId(),
                topCategory.getId(), "M", 0L, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice(), mall.getName(), mall.getUrl())
                );
    }

    @DisplayName("서브 카테고리별로 상품을 조회할 수 있다.")
    @Test
    void productWithSubCategory() {
        //given
        SubCategory topSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("top")
                .build());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall, topSubCategory));

        //when
        Page<ResponseProductPreviewDto> target = productRepository.productWithSubCategory(mall.getId(),
                topSubCategory.getId(), "M", 0L, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice(), mall.getName(), mall.getUrl())
                );
    }

    @DisplayName("키워드로 상품을 조회할 수 있다.")
    @Test
    void findProductByKeyword() {
        //given
        Product product = productRepository.save(createProduct());

        //when
        Page<ResponseProductPreviewDto> target = productRepository.searchProduct(
                product.getName(), "M", 0L, PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice())
                );
    }

    @DisplayName("키워드로 상품 연관 검색을 할 수 있다.")
    @Test
    void findProductByKeywordOnRelatedSearch() {
        //given
        Product product = productRepository.save(createProduct());

        //when
        List<RelatedSearchDto> target = productRepository.relatedSearch("pro");

        //then
        assertThat(target).hasSize(1)
                .extracting("id", "name", "image")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getName(), product.getImage())
                );
    }

    @DisplayName("카테고리별 상품 개수를 조회할 수 있다.")
    @Test
    void productCountWithCategory() {
        //given
        Category topCategory = categoryRepository.save(Category.builder()
                .name("top")
                .build());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall, topCategory));

        //when
        Long target = productRepository.productCountWithCategory(topCategory.getId());

        //then
        assertThat(target).isEqualTo(1L);
    }

    @DisplayName("서브 카테고리별 상품 개수를 조회할 수 있다.")
    @Test
    void productCountWithSubcategory() {
        //given
        SubCategory topSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("top")
                .build());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall, topSubCategory));

        //when
        Long target = productRepository.productCountWithSubCategory(topSubCategory.getId());

        //then
        assertThat(target).isEqualTo(1L);
    }

    @DisplayName("쇼핑몰 내 카테고리별 상품 개수를 조회할 수 있다.")
    @Test
    void productCountWithCategoryOfMall() {
        //given
        Category topCategory = categoryRepository.save(Category.builder()
                .name("top")
                .build());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall, topCategory));

        //when
        Long target = productRepository.productCountWithCategoryOfMall(mall.getName(), topCategory.getId());

        //then
        assertThat(target).isEqualTo(1L);
    }

    @DisplayName("쇼핑몰 내 서브 카테고리별 상품 개수를 조회할 수 있다.")
    @Test
    void productCountWithSubCategoryOfMall() {
        //given
        SubCategory topSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("top")
                .build());
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall, topSubCategory));

        //when
        Long target = productRepository.productCountWithSubCategoryOfMall(mall.getName(), topSubCategory.getId());

        //then
        assertThat(target).isEqualTo(1L);
    }

    @DisplayName("아우터 상품을 상세 조회할 수 있다.")
    @Test
    void outerProductDetail() {
        //given
        Category outerCategory = categoryRepository.save(Category.builder()
                .name("outer")
                .build());
        SubCategory outerSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("coat")
                .category(outerCategory)
                .build());
        Mall mall = mallRepository.save(createMall());
        Outer outerSize = outerRepository.save(Outer.builder()
                .full(100.0)
                .chest(100.0)
                .sleeve(100.0)
                .build());
        Size size = sizeRepository.save(Size.builder()
                .name("M")
                .outer(outerSize)
                .build());

        Product product = productRepository.save(createProduct(mall, outerCategory, outerSubCategory, size));

        //when
        OuterProductDto target = productRepository.outerProductDetail(product.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "mallName", "mallUrl",
                        "mallImage", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        product.getImage(),
                        product.getName(),
                        product.getGender(),
                        product.getPrice(),
                        mall.getName(),
                        mall.getUrl(),
                        mall.getImage(),
                        product.getOrigin(),
                        outerCategory.getName(),
                        outerSubCategory.getName()
                );
    }

    @DisplayName("상의 상품을 상세 조회할 수 있다.")
    @Test
    void topProductDetail() {
        //given
        Category topCategory = categoryRepository.save(Category.builder()
                .name("top")
                .build());
        SubCategory topSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("shirt")
                .category(topCategory)
                .build());
        Mall mall = mallRepository.save(createMall());
        Top topSize = topRepository.save(Top.builder()
                .full(100.0)
                .chest(100.0)
                .sleeve(100.0)
                .build());
        Size size = sizeRepository.save(Size.builder()
                .name("M")
                .top(topSize)
                .build());

        Product product = productRepository.save(createProduct(mall, topCategory, topSubCategory, size));

        //when
        TopProductDto target = productRepository.topProductDetail(product.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "mallName", "mallUrl",
                        "mallImage", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        product.getImage(),
                        product.getName(),
                        product.getGender(),
                        product.getPrice(),
                        mall.getName(),
                        mall.getUrl(),
                        mall.getImage(),
                        product.getOrigin(),
                        topCategory.getName(),
                        topSubCategory.getName()
                );
    }

    @DisplayName("원피스 상품을 상세 조회할 수 있다.")
    @Test
    void dressProductDetail() {
        //given
        Category dressCategory = categoryRepository.save(Category.builder()
                .name("dress")
                .build());
        SubCategory dressSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("dress")
                .category(dressCategory)
                .build());
        Mall mall = mallRepository.save(createMall());
        Dress dressSize = dressRepository.save(Dress.builder()
                .full(100.0)
                .chest(100.0)
                .build());
        Size size = sizeRepository.save(Size.builder()
                .name("M")
                .dress(dressSize)
                .build());

        Product product = productRepository.save(createProduct(mall, dressCategory, dressSubCategory, size));

        //when
        DressProductDto target = productRepository.dressProductDetail(product.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "mallName", "mallUrl",
                        "mallImage", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        product.getImage(),
                        product.getName(),
                        product.getGender(),
                        product.getPrice(),
                        mall.getName(),
                        mall.getUrl(),
                        mall.getImage(),
                        product.getOrigin(),
                        dressCategory.getName(),
                        dressSubCategory.getName()
                );
    }

    @DisplayName("하의 상품을 상세 조회할 수 있다.")
    @Test
    void bottomProductDetail() {
        //given
        Category bottomCategory = categoryRepository.save(Category.builder()
                .name("bottom")
                .build());
        SubCategory bottomSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("pants")
                .category(bottomCategory)
                .build());
        Mall mall = mallRepository.save(createMall());
        Bottom bottomSize = bottomRepository.save(Bottom.builder()
                .full(100.0)
                .waist(100.0)
                .bottomWidth(100.0)
                .build());
        Size size = sizeRepository.save(Size.builder()
                .name("M")
                .bottom(bottomSize)
                .build());

        Product product = productRepository.save(createProduct(mall, bottomCategory, bottomSubCategory, size));

        //when
        BottomProductDto target = productRepository.bottomProductDetail(product.getId());

        //then
        assertThat(target)
                .extracting("productImage", "productName", "productGender", "price", "mallName", "mallUrl",
                        "mallImage", "origin", "category", "subCategory")
                .containsExactlyInAnyOrder(
                        product.getImage(),
                        product.getName(),
                        product.getGender(),
                        product.getPrice(),
                        mall.getName(),
                        mall.getUrl(),
                        mall.getImage(),
                        product.getOrigin(),
                        bottomCategory.getName(),
                        bottomSubCategory.getName()
                );
    }

    @DisplayName("연령대별 좋아요 비율을 조회할 수 있다.")
    @Test
    void findPopularAgeRangePercents() {
        //given
        User user = userRepository.save(createUser());
        Product product = productRepository.save(createProduct());
        favoriteRepository.save(createFavorite(user, product));

        //when
        List<Integer> target = productRepository.findPopularAgeRangePercents(product.getId());

        //then
        assertThat(target)
                .containsExactlyInAnyOrder(
                        100, 0, 0, 0, 0, 0
                );
    }

    @DisplayName("실시간 조회수 기준으로 상품을 조회할 수 있다.")
    @Test
    void findProductsByTimeRank() {
        //given
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));
        Product product2 = productRepository.save(createProduct(mall));

        //when
        List<ResponseProductPreviewDto> target = productRepository.timeRank("M");

        //then
        assertThat(target).hasSize(2)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice(), mall.getName(), mall.getUrl()),
                        tuple(product2.getId(), product2.getImage(), product2.getName(), product2.getPrice(), mall.getName(), mall.getUrl())
                );
    }

    @DisplayName("최근 업데이트된 상품의 업데이트 시각을 조회할 수 있다.")
    @Test
    void findMaxUpdatedAt() {
        //given
        Mall mall = mallRepository.save(createMall());
        Product product = productRepository.save(createProduct(mall));
        Product product2 = productRepository.save(createProduct(mall));

        //when
        Optional<LocalDateTime> target = productRepository.maxUpdatedAt();

        //then
        assertThat(target.get()).isEqualTo(product2.getUpdatedAt());
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

    private Product createProduct(Mall mall, Category category) {
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
                .category(category)
                .build();
    }

    private Product createProduct(Mall mall, SubCategory subCategory) {
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
                .subCategory(subCategory)
                .build();
    }

    private Product createProduct(Mall mall, Category category, SubCategory subCategory, Size size) {
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
                .category(category)
                .subCategory(subCategory)
                .sizes(List.of(size))
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

    private Favorite createFavorite(User user, Product product) {
        return Favorite.builder()
                .user(user)
                .product(product)
                .build();
    }
}