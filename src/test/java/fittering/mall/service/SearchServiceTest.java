package fittering.mall.service;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.controller.dto.response.ResponseRelatedSearchMallDto;
import fittering.mall.controller.dto.response.ResponseRelatedSearchProductDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.repository.*;
import fittering.mall.service.dto.SignUpDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.RestPage;
import fittering.mall.service.dto.MallDto;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
class SearchServiceTest extends IntegrationTestSupport {

    @Autowired
    private SearchService searchService;

    @Autowired
    private MallService mallService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private TopRepository topRepository;

    @Autowired
    private MallRepository mallRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        topRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
    }

    @DisplayName("키워드로 상품을 검색할 수 있다.")
    @Test
    void searchProductByKeyword() {
        //given
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));

        Product product = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(2, topCategory, topSubCategory, mall));

        //when
        RestPage<ResponseProductPreviewDto> target = searchService.products("product", "M", 0L, PageRequest.of(0, 10));

        //then
        assertThat(target)
                .extracting("productId", "productImage", "productName", "price")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getImage(), product.getName(), product.getPrice()),
                        tuple(product2.getId(), product2.getImage(), product2.getName(), product2.getPrice())
                );
    }

    @DisplayName("키워드로 연관된 상품을 검색할 수 있다.")
    @Test
    void relatedSearchProducts() {
        //given
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));

        Product product = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(2, topCategory, topSubCategory, mall));

        //when
        List<ResponseRelatedSearchProductDto> target = searchService.relatedSearchProducts("product");

        //then
        assertThat(target)
                .extracting("id", "name", "image")
                .containsExactlyInAnyOrder(
                        tuple(product.getId(), product.getName(), product.getImage()),
                        tuple(product2.getId(), product2.getName(), product2.getImage())
                );
    }

    @DisplayName("키워드로 연관된 쇼핑몰은 검색할 수 있다.")
    @Test
    void relatedSearchMalls() {
        //given
        Mall mall = mallService.save(createMall(1));
        Mall mall2 = mallService.save(createMall(2));

        //when
        List<ResponseRelatedSearchMallDto> target = searchService.relatedSearchMalls("testMall");

        //then
        assertThat(target)
                .extracting("id", "name", "image")
                .containsExactlyInAnyOrder(
                        tuple(mall.getId(), mall.getName(), mall.getImage()),
                        tuple(mall2.getId(), mall2.getName(), mall2.getImage())
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