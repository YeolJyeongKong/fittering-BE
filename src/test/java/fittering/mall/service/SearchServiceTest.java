package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.dto.service.SignUpDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.RestPage;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class SearchServiceTest {

    @Autowired
    SearchService searchService;
    @Autowired
    RankService rankService;
    @Autowired
    MallService mallService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private Category topCategory;
    private SubCategory topSubCategory;
    private Mall mall;
    private Mall mall2;
    private Product product;
    private Product product2;
    private Product product3;
    private Product product4;
    private List<String> descImgsStr;
    private List<ProductDescription> descImgs;
    private List<ProductDescription> descImgs2;
    private List<ProductDescription> descImgs3;
    private List<ProductDescription> descImgs4;
    private User user;

    @BeforeEach
    void setUp() {
        topCategory = categoryService.save("top");
        topSubCategory = categoryService.saveSubCategory("top", "shirt");
        mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        mall2 = mallService.save(new MallDto(2L, "testMall2", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        descImgsStr = List.of("descImage.jpg");
        product = productService.save(Product.builder()
                .price(10000)
                .name("A 티셔츠")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/1")
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product2 = productService.save(Product.builder()
                .price(10000)
                .name("A 셔츠")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/2")
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product3 = productService.save(Product.builder()
                .price(10000)
                .name("B 티셔츠")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/3")
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product4 = productService.save(Product.builder()
                .price(10000)
                .name("ABC 스웨터")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/4")
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        descImgs = List.of(new ProductDescription(descImgsStr.get(0), product));
        descImgs2 = List.of(new ProductDescription(descImgsStr.get(0), product2));
        descImgs3 = List.of(new ProductDescription(descImgsStr.get(0), product3));
        descImgs4 = List.of(new ProductDescription(descImgsStr.get(0), product4));
    }

    @AfterEach
    void End() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
    }

    @Test
    void products() {
        RestPage<ResponseProductPreviewDto> shirtProducts = searchService.products("셔츠", "M", 0L, PageRequest.of(0, 10));
        List<Product> products = List.of(product, product2, product3);
        for (int i=0; i<products.size(); i++) {
            Product product = products.get(i);
            ResponseProductPreviewDto shirtProduct = shirtProducts.getContent().get(i);
            compareProduct(product, shirtProduct);
        }
    }

    private static void compareProduct(Product savedProduct, ResponseProductPreviewDto productService) {
        assertThat(productService.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productService.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productService.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(productService.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productService.getMallName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(productService.getMallUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }
}