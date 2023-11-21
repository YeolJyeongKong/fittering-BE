package fittering.mall.service;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.controller.dto.response.ResponseMallDto;
import fittering.mall.controller.dto.response.ResponseMallNameAndIdDto;
import fittering.mall.controller.dto.response.ResponseMallWithProductDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.repository.CategoryRepository;
import fittering.mall.repository.MallRepository;
import fittering.mall.repository.ProductRepository;
import fittering.mall.repository.SubCategoryRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import fittering.mall.service.dto.MallDto;
import fittering.mall.domain.entity.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class MallServiceTest extends IntegrationTestSupport {

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
        productRepository.deleteAllInBatch();
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
    }

    @DisplayName("쇼핑몰을 저장할 수 있다.")
    @Test
    void saveMall() {
        //given
        MallDto request = createMall();

        //when
        Mall savedMall = mallService.save(request);

        //then
        assertThat(savedMall.getId()).isNotNull();
        assertThat(savedMall)
                .extracting("name", "url", "image", "description")
                .containsExactlyInAnyOrder(
                        request.getName(),
                        request.getUrl(),
                        request.getImage(),
                        request.getDescription()
                );
    }

    @DisplayName("쇼핑몰을 id로 조회할 수 있다.")
    @Test
    void findMallById() {
        //given
        Long userId = 1L;
        Mall savedMall = mallService.save(createMall());

        //when
        ResponseMallDto target = mallService.findById(userId, savedMall.getId());

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("name", "url", "image", "description")
                .containsExactlyInAnyOrder(
                        savedMall.getName(),
                        savedMall.getUrl(),
                        savedMall.getImage(),
                        savedMall.getDescription()
                );
    }

    @DisplayName("저장되지 않은 쇼핑몰을 id로 조회할 수 없다.")
    @Test
    void findNotSavedMallById() {
        //given
        Long userId = 1L;
        Long mallId = 1L;

        //when //then
        assertThatThrownBy(() -> mallService.findById(userId, mallId))
                .isInstanceOf(NoResultException.class)
                .hasMessage("mall dosen't exist");
    }

    @DisplayName("쇼핑몰을 이름으로 조회할 수 있다.")
    @Test
    void findMallByName() {
        //given
        MallDto savedMall = createMall();
        mallService.save(savedMall);

        //when
        Mall target = mallService.findByName(savedMall.getName());

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("name", "url", "image", "description")
                .containsExactlyInAnyOrder(
                        savedMall.getName(),
                        savedMall.getUrl(),
                        savedMall.getImage(),
                        savedMall.getDescription()
                );
    }

    @DisplayName("저장되지 이름으로 쇼핑몰을 조회할 수 없다.")
    @Test
    void findNotSavedMallByName() {
        //given
        String mallName = "열졍콩몰";

        //when //then
        assertThatThrownBy(() -> mallService.findByName(mallName))
                .isInstanceOf(NoResultException.class)
                .hasMessage("mall dosen't exist");
    }

    @DisplayName("쇼핑몰에 상품을 추가할 수 있다.")
    @Test
    void addProductOnMall() {
        //given
        Mall savedMall = mallService.save(createMall());
        Product savedProduct = productService.save(createProduct(1));

        //when
        mallService.addProduct(savedMall.getName(), savedProduct.getId());

        //then
        List<ResponseProductPreviewDto> target = mallService.findProducts(savedMall.getName());
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice())
                );
    }

    @DisplayName("쇼핑몰에 저장된 상품을 조회할 수 있다.")
    @Test
    void getSavedProductOfMall() {
        //given
        Mall savedMall = mallService.save(createMall());
        Product savedProduct = productService.save(createProduct(1));

        //when
        mallService.findProducts(savedMall.getName());

        //then
        List<ResponseProductPreviewDto> target = mallService.findProducts(savedMall.getName());
        assertThat(target).hasSize(1)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(savedProduct.getId(), savedProduct.getImage(), savedProduct.getName(), savedProduct.getPrice())
                );
    }

    @DisplayName("저장된 쇼핑몰의 id와 이름 리스트를 조회할 수 있다.")
    @Test
    void getMallIdAndNameList() {
        //given
        Mall savedMall = mallService.save(createMall());
        Mall savedMall2 = mallService.save(createMall());
        Mall savedMall3 = mallService.save(createMall());

        //when
        List<ResponseMallNameAndIdDto> target = mallService.findMallNameAndIdList();

        //then
        assertThat(target).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(savedMall.getId(), savedMall.getName()),
                        tuple(savedMall2.getId(), savedMall2.getName()),
                        tuple(savedMall3.getId(), savedMall3.getName())
                );
    }

    @DisplayName("저장된 쇼핑몰 리스트를 조회할 수 있다.")
    @Test
    void getMallList() {
        //given
        Mall savedMall = mallService.save(createMall());
        Mall savedMall2 = mallService.save(createMall());
        Mall savedMall3 = mallService.save(createMall());

        //when
        List<ResponseMallWithProductDto> target = mallService.findAll(1L);

        //then
        assertThat(target).hasSize(3)
                .extracting("id", "name", "image", "description")
                .containsExactlyInAnyOrder(
                        tuple(savedMall.getId(), savedMall.getName(), savedMall.getImage(), savedMall.getDescription()),
                        tuple(savedMall2.getId(), savedMall2.getName(), savedMall2.getImage(), savedMall2.getDescription()),
                        tuple(savedMall3.getId(), savedMall3.getName(), savedMall3.getImage(), savedMall3.getDescription())
                );
    }

    private MallDto createMall() {
        return MallDto.builder()
                .name("열졍콩몰")
                .url("https://www.test-mall.com")
                .image("image.jpg")
                .description("description")
                .view(0)
                .products(List.of())
                .build();
    }

    private Product createProduct(int number) {
        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.findByName("열졍콩몰");
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