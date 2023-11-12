package fittering.mall.service;

import fittering.mall.repository.*;
import fittering.mall.service.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class SizeServiceTest {

    @Autowired
    private SizeService sizeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
    private MallRepository mallRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        sizeRepository.deleteAllInBatch();
        outerRepository.deleteAllInBatch();
        topRepository.deleteAllInBatch();
        dressRepository.deleteAllInBatch();
        bottomRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
    }

    @DisplayName("아우터 상품의 사이즈를 구할 수 있다.")
    @Test
    void getOuterSize() {
        //given
        OuterDto outerDto = OuterDto.builder()
                .name("size")
                .full(100.0)
                .shoulder(100.0)
                .chest(100.0)
                .sleeve(100.0)
                .build();
        Category outerCategory = categoryService.save("outer");
        SubCategory outerSubCategory = categoryService.saveSubCategory("outer", "coat");
        Mall mall = mallService.save(createMall(1));
        Product product = productService.save(createProduct(1, outerCategory, outerSubCategory, mall));

        //when
        Outer target = sizeService.saveOuter(outerDto, product).getOuter();

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("full", "shoulder", "chest", "sleeve")
                .containsExactlyInAnyOrder(
                        100.0, 100.0, 100.0, 100.0
                );
    }

    @DisplayName("상의 상품의 사이즈를 구할 수 있다.")
    @Test
    void getTopSize() {
        //given
        TopDto topDto = TopDto.builder()
                .name("size")
                .full(100.0)
                .shoulder(100.0)
                .chest(100.0)
                .sleeve(100.0)
                .build();
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product = productService.save(createProduct(1, topCategory, topSubCategory, mall));

        //when
        Top target = sizeService.saveTop(topDto, product).getTop();

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("full", "shoulder", "chest", "sleeve")
                .containsExactlyInAnyOrder(
                        100.0, 100.0, 100.0, 100.0
                );
    }

    @DisplayName("원피스 상품의 사이즈를 구할 수 있다.")
    @Test
    void getDressSize() {
        //given
        DressDto dressDto = DressDto.builder()
                .name("size")
                .full(100.0)
                .shoulder(100.0)
                .chest(100.0)
                .waist(100.0)
                .armHall(100.0)
                .hip(100.0)
                .sleeve(100.0)
                .sleeveWidth(100.0)
                .bottomWidth(100.0)
                .build();
        Category dressCategory = categoryService.save("dress");
        SubCategory dressSubCategory = categoryService.saveSubCategory("dress", "dress");
        Mall mall = mallService.save(createMall(1));
        Product product = productService.save(createProduct(1, dressCategory, dressSubCategory, mall));

        //when
        Dress target = sizeService.saveDress(dressDto, product).getDress();

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("full", "shoulder", "chest", "waist", "armHall", "hip", "sleeve", "sleeveWidth", "bottomWidth")
                .containsExactlyInAnyOrder(
                        100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0
                );
    }

    @DisplayName("하의 상품의 사이즈를 구할 수 있다.")
    @Test
    void getBottomSize() {
        //given
        BottomDto bottomDto = BottomDto.builder()
                .name("size")
                .full(100.0)
                .waist(100.0)
                .thigh(100.0)
                .rise(100.0)
                .bottomWidth(100.0)
                .hipWidth(100.0)
                .build();
        Category bottomCategory = categoryService.save("bottom");
        SubCategory bottomSubCategory = categoryService.saveSubCategory("bottom", "pants");
        Mall mall = mallService.save(createMall(1));
        Product product = productService.save(createProduct(1, bottomCategory, bottomSubCategory, mall));

        //when
        Bottom target = sizeService.saveBottom(bottomDto, product).getBottom();

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("full", "waist", "thigh", "rise", "bottomWidth", "hipWidth")
                .containsExactlyInAnyOrder(
                        100.0, 100.0, 100.0, 100.0, 100.0, 100.0
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