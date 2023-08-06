package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.ResponseBottomDto;
import fittering.mall.domain.dto.controller.response.ResponseProductCategoryDto;
import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.dto.controller.response.ResponseTopDto;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.dto.service.SignUpDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    MallService mallService;
    @Autowired
    UserService userService;

    private Category topCategory;
    private Category bottomCategory;
    private SubCategory topSubCategory;
    private SubCategory bottomSubCategory;
    private Mall mall;
    private List<String> descImgsStr;
    private List<DescriptionImage> descImgs;
    private List<DescriptionImage> descImgs2;
    private List<DescriptionImage> descImgs3;
    private List<DescriptionImage> descImgs4;
    private List<DescriptionImage> descImgs5;
    private Product product;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;
    private User user;

    @BeforeEach
    void setUp() {
        topCategory = categoryService.save("top");
        bottomCategory = categoryService.save("bottom");
        topSubCategory = categoryService.saveSubCategory("top", "shirt");
        bottomSubCategory = categoryService.saveSubCategory("bottom", "pants");
        mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        descImgsStr = List.of("descImage.jpg");
        product = productService.save(Product.builder()
                .price(10000)
                .name("tp1")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product2 = productService.save(Product.builder()
                .price(10000)
                .name("tp2")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product3 = productService.save(Product.builder()
                .price(10000)
                .name("tp3")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product4 = productService.save(Product.builder()
                .price(10000)
                .name("tp4")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        product5 = productService.save(Product.builder()
                .price(10000)
                .name("tp5")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        descImgs = List.of(new DescriptionImage(descImgsStr.get(0), product));
        descImgs2 = List.of(new DescriptionImage(descImgsStr.get(0), product2));
        descImgs3 = List.of(new DescriptionImage(descImgsStr.get(0), product3));
        descImgs4 = List.of(new DescriptionImage(descImgsStr.get(0), product4));
        descImgs5 = List.of(new DescriptionImage(descImgsStr.get(0), product5));
    }

    @Test
    void findById() {
        Product findProductById = productService.findById(product.getId());
        checkProduct(product, findProductById);
    }

    @Test
    void productWithCategory() {
        Page<ResponseProductPreviewDto> findProductByCategory =
                productService.productWithCategory(topCategory.getId(), "M", 0L, PageRequest.of(0, 10));
        compareProduct(product, findProductByCategory.getContent().get(0));
        compareProduct(product2, findProductByCategory.getContent().get(1));
        compareProduct(product3, findProductByCategory.getContent().get(2));
        compareProduct(product4, findProductByCategory.getContent().get(3));
        compareProduct(product5, findProductByCategory.getContent().get(4));
    }

    @Test
    void productWithCategoryOfMall() {
        Page<ResponseProductPreviewDto> findProductByCategoryOfMall =
                productService.productWithCategoryOfMall(mall.getId(), topCategory.getId(), "M", 0L, PageRequest.of(0, 10));
        compareProduct(product, findProductByCategoryOfMall.getContent().get(0));
        compareProduct(product2, findProductByCategoryOfMall.getContent().get(1));
        compareProduct(product3, findProductByCategoryOfMall.getContent().get(2));
        compareProduct(product4, findProductByCategoryOfMall.getContent().get(3));
        compareProduct(product5, findProductByCategoryOfMall.getContent().get(4));
    }

    @Test
    void multipleProductCountWithCategory() {
        Product topProduct = productService.save(Product.builder()
                .price(10000)
                .name("tp1")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        Product topProduct2 = productService.save(Product.builder()
                .price(10000)
                .name("tp2")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        Product bottomProduct = productService.save(Product.builder()
                .price(10000)
                .name("tp2")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall)
                .build());
        Product bottomProduct2 = productService.save(Product.builder()
                .price(10000)
                .name("bp1")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall)
                .build());
        Product bottomProduct3 = productService.save(Product.builder()
                .price(10000)
                .name("bp3")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall)
                .build());
        Product bottomProduct4 = productService.save(Product.builder()
                .price(10000)
                .name("bp4")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall)
                .build());
        List<DescriptionImage> topDescriptionImgs = List.of(new DescriptionImage(descImgsStr.get(0), topProduct));
        List<DescriptionImage> topDescriptionImgs2 = List.of(new DescriptionImage(descImgsStr.get(0), topProduct2));
        List<DescriptionImage> bottomDescriptionImgs = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct));
        List<DescriptionImage> bottomDescriptionImgs2 = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct2));
        List<DescriptionImage> bottomDescriptionImgs3 = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct3));
        List<DescriptionImage> bottomDescriptionImgs4 = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct4));

        List<ResponseProductCategoryDto> findProductCountsOnCategory = productService.multipleProductCountWithCategory();
        assertThat(findProductCountsOnCategory.get(0).getCount()).isEqualTo(7);
        assertThat(findProductCountsOnCategory.get(1).getCount()).isEqualTo(4);
    }

    @Test
    void productCountWithCategoryOfMall() {
        Product topProduct = productService.save(Product.builder()
                .price(10000)
                .name("tp1")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        Product topProduct2 = productService.save(Product.builder()
                .price(10000)
                .name("tp2")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall)
                .build());
        List<DescriptionImage> topDescriptionImgs = List.of(new DescriptionImage(descImgsStr.get(0), topProduct));
        List<DescriptionImage> topDescriptionImgs2 = List.of(new DescriptionImage(descImgsStr.get(0), topProduct2));

        Mall mall2 = mallService.save(new MallDto(2L, "testMall2", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        Product bottomProduct = productService.save(Product.builder()
                .price(10000)
                .name("tp2")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall2)
                .build());
        Product bottomProduct2 = productService.save(Product.builder()
                .price(10000)
                .name("bp1")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall2)
                .build());
        Product bottomProduct3 = productService.save(Product.builder()
                .price(10000)
                .name("bp3")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall2)
                .build());
        Product bottomProduct4 = productService.save(Product.builder()
                .price(10000)
                .name("bp4")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall2)
                .build());
        List<DescriptionImage> bottomDescriptionImgs = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct));
        List<DescriptionImage> bottomDescriptionImgs2 = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct2));
        List<DescriptionImage> bottomDescriptionImgs3 = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct3));
        List<DescriptionImage> bottomDescriptionImgs4 = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct4));


        List<ResponseProductCategoryDto> findProductCountsOnCategoryOfMall = productService.productCountWithCategoryOfMall(mall.getId());
        assertThat(findProductCountsOnCategoryOfMall.get(0).getCount()).isEqualTo(7);
        assertThat(findProductCountsOnCategoryOfMall.get(1).getCount()).isEqualTo(0);

        List<ResponseProductCategoryDto> findProductCountsOnCategoryOfMall2 = productService.productCountWithCategoryOfMall(mall2.getId());
        assertThat(findProductCountsOnCategoryOfMall2.get(0).getCount()).isEqualTo(0);
        assertThat(findProductCountsOnCategoryOfMall2.get(1).getCount()).isEqualTo(4);
    }

    @Test
    void recommendProduct() {
        List<Long> productIds = new ArrayList<>(){{
            add(product.getId());
            add(product2.getId());
            add(product3.getId());
            add(product4.getId());
            add(product5.getId());
        }};

        //preview
        List<ResponseProductPreviewDto> recommendedProductsPreview = productService.recommendProduct(productIds, true);
        assertThat(recommendedProductsPreview.size()).isEqualTo(4);
        compareProduct(product, recommendedProductsPreview.get(0));
        compareProduct(product2, recommendedProductsPreview.get(1));
        compareProduct(product3, recommendedProductsPreview.get(2));
        compareProduct(product4, recommendedProductsPreview.get(3));

        //not preview
        List<ResponseProductPreviewDto> recommendedProducts = productService.recommendProduct(productIds, false);
        assertThat(recommendedProducts.size()).isEqualTo(5);
        compareProduct(product, recommendedProducts.get(0));
        compareProduct(product2, recommendedProducts.get(1));
        compareProduct(product3, recommendedProducts.get(2));
        compareProduct(product4, recommendedProducts.get(3));
        compareProduct(product5, recommendedProducts.get(4));
    }

    @Test
    void topProductDetail() {
        ResponseTopDto topProductDto = productService.topProductDetail(product.getId());
        assertThat(topProductDto.getProductImage()).isEqualTo(product.getImage());
        assertThat(topProductDto.getProductName()).isEqualTo(product.getName());
        assertThat(topProductDto.getProductGender()).isEqualTo(product.getGender());
        assertThat(topProductDto.getPrice()).isEqualTo(product.getPrice());

        Mall savedMall = product.getMall();
        assertThat(topProductDto.getMallName()).isEqualTo(savedMall.getName());
        assertThat(topProductDto.getMallUrl()).isEqualTo(savedMall.getUrl());
        assertThat(topProductDto.getMallImage()).isEqualTo(savedMall.getImage());

        assertThat(topProductDto.getCategory()).isEqualTo(product.getCategory().getName());
    }

    @Test
    void bottomProductDetail() {
        Product bottomProduct = productService.save(Product.builder()
                .price(10000)
                .name("tp2")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .category(bottomCategory)
                .subCategory(bottomSubCategory)
                .mall(mall)
                .build());
        List<DescriptionImage> bottomDescriptionImgs = List.of(new DescriptionImage(descImgsStr.get(0), bottomProduct));

        ResponseBottomDto bottomProductDto = productService.bottomProductDetail(bottomProduct.getId());
        assertThat(bottomProductDto.getProductImage()).isEqualTo(bottomProduct.getImage());
        assertThat(bottomProductDto.getProductName()).isEqualTo(bottomProduct.getName());
        assertThat(bottomProductDto.getProductGender()).isEqualTo(bottomProduct.getGender());
        assertThat(bottomProductDto.getPrice()).isEqualTo(bottomProduct.getPrice());

        Mall savedMall = bottomProduct.getMall();
        assertThat(bottomProductDto.getMallName()).isEqualTo(savedMall.getName());
        assertThat(bottomProductDto.getMallUrl()).isEqualTo(savedMall.getUrl());
        assertThat(bottomProductDto.getMallImage()).isEqualTo(savedMall.getImage());

        assertThat(bottomProductDto.getCategory()).isEqualTo(bottomProduct.getCategory().getName());
    }

    @Test
    void updateView() {
        productService.updateView(product.getId());
        Product findProduct = productService.findById(product.getId());
        assertThat(findProduct.getView()).isEqualTo(1);
    }

    @Test
    void recentRecommendationTest() {
        productService.saveRecentRecommendation(user.getId(), product.getId());
        productService.saveRecentRecommendation(user.getId(), product2.getId());
        productService.saveRecentRecommendation(user.getId(), product3.getId());
        productService.saveRecentRecommendation(user.getId(), product4.getId());
        productService.saveRecentRecommendation(user.getId(), product5.getId());

        List<Product> products = productService.productWithRecentRecommendation(user.getId());
        assertThat(products.size()).isEqualTo(5);
        compareProduct(product, products.get(0));
        compareProduct(product2, products.get(1));
        compareProduct(product3, products.get(2));
        compareProduct(product4, products.get(3));
        compareProduct(product5, products.get(4));
    }

    @Test
    void userRecommendationTest() {
        productService.saveUserRecommendation(user.getId(), product.getId());
        productService.saveUserRecommendation(user.getId(), product2.getId());
        productService.saveUserRecommendation(user.getId(), product3.getId());
        productService.saveUserRecommendation(user.getId(), product4.getId());
        productService.saveUserRecommendation(user.getId(), product5.getId());

        List<Product> products = productService.productWithUserRecommendation(user.getId());
        assertThat(products.size()).isEqualTo(5);
        compareProduct(product, products.get(0));
        compareProduct(product2, products.get(1));
        compareProduct(product3, products.get(2));
        compareProduct(product4, products.get(3));
        compareProduct(product5, products.get(4));
    }

    private static void checkProduct(Product savedProduct, Product findProduct) {
        assertThat(savedProduct.getId()).isEqualTo(findProduct.getId());
        assertThat(savedProduct.getImage()).isEqualTo(findProduct.getImage());
        assertThat(savedProduct.getName()).isEqualTo(findProduct.getName());
        assertThat(savedProduct.getGender()).isEqualTo(findProduct.getGender());
        assertThat(savedProduct.getPrice()).isEqualTo(findProduct.getPrice());
        assertThat(savedProduct.getView()).isEqualTo(findProduct.getView());
        assertThat(savedProduct.getTimeView()).isEqualTo(findProduct.getTimeView());
        assertThat(savedProduct.getType()).isEqualTo(findProduct.getType());
        savedProduct.getDescriptionImages().forEach(descImg -> {
            assertThat(findProduct.getDescriptionImages().contains(descImg)).isTrue();
        });
    }

    private static void compareProduct(Product savedProduct, ResponseProductPreviewDto productPreviewDto) {
        assertThat(productPreviewDto.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productPreviewDto.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productPreviewDto.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(productPreviewDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productPreviewDto.getMallName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(productPreviewDto.getMallUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }

    private static void compareProduct(Product savedProduct, Product product) {
        assertThat(product.getId()).isEqualTo(savedProduct.getId());
        assertThat(product.getImage()).isEqualTo(savedProduct.getImage());
        assertThat(product.getName()).isEqualTo(savedProduct.getName());
        assertThat(product.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(product.getMall().getName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(product.getMall().getUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }
}