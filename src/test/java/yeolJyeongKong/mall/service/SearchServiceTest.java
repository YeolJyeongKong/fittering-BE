package yeolJyeongKong.mall.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import yeolJyeongKong.mall.domain.RestPage;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.ProductDetailDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.entity.*;

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

    private Category topCategory;
    private Mall mall;
    private Mall mall2;
    private List<String> descImgsStr;
    private List<DescriptionImage> descImgs;
    private Product savedProduct;
    private Product product2;
    private Product product3;
    private Product product4;
    private User user;

    @BeforeEach
    void setUp() {
        topCategory = categoryService.save("top");
        mall = mallService.save(new MallDto("testMall", "testMall.com",
                "image.jpg", "desc", new ArrayList<>()));
        mall2 = mallService.save(new MallDto("testMall2", "testMall.com",
                "image.jpg", "desc", new ArrayList<>()));
        user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        savedProduct = productService.save(new Product(
                new ProductDetailDto(10000, "A 티셔츠", "M", 0,
                        "image.jpg", "top",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, mall, descImgs));
        product2 = productService.save(new Product(
                new ProductDetailDto(10000, "A 셔츠", "M", 0,
                        "image.jpg", "top",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, mall, descImgs));
        product3 = productService.save(new Product(
                new ProductDetailDto(10000, "B 티셔츠", "M", 0,
                        "image.jpg", "top",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, mall, descImgs));
        product4 = productService.save(new Product(
                new ProductDetailDto(10000, "ABC 스웨터", "M", 0,
                        "image.jpg", "top",
                        "testMall2", null, null, null, null, descImgsStr),
                topCategory, mall2, descImgs));
    }

    @Test
    void products() {
        RestPage<ProductPreviewDto> shirtProducts = searchService.products("셔츠", "", 0L, PageRequest.of(0, 10));
        List<Product> products = new ArrayList<>(){{
            add(savedProduct);
            add(product2);
            add(product3);
        }};

        for (int i=0; i<products.size(); i++) {
            Product product = products.get(i);
            ProductPreviewDto shirtProduct = shirtProducts.getContent().get(i);
            compareProduct(product, shirtProduct);
        }
    }

    private static void compareProduct(Product savedProduct, ProductPreviewDto productService) {
        assertThat(productService.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productService.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productService.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(productService.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productService.getMallName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(productService.getMallUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }
}