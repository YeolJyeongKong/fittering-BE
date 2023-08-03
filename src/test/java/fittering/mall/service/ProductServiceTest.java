package fittering.mall.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.dto.*;
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
    private Product savedProduct;
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
        descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        savedProduct = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "top", "testMall", "shirt",
                        null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product2 = productService.save(new Product(
                new ProductDetailDto(10000, "tp2", "M", 0,
                        "image.jpg", "top", "shirt", "testMall",
                        null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product3 = productService.save(new Product(
                new ProductDetailDto(10000, "tp3", "M", 0,
                        "image.jpg", "top", "shirt", "testMall",
                        null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product4 = productService.save(new Product(
                new ProductDetailDto(10000, "tp4", "M", 0,
                        "image.jpg", "top", "shirt", "testMall",
                        null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product5 = productService.save(new Product(
                new ProductDetailDto(10000, "tp5", "M", 0,
                        "image.jpg", "top", "shirt", "testMall",
                        null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
    }

    @Test
    void findById() {
        Product findProductById = productService.findById(savedProduct.getId());
        checkProduct(savedProduct, findProductById);
    }

    @Test
    void productWithCategory() {
        Page<ProductPreviewDto> findProductByCategory =
                productService.productWithCategory(topCategory.getId(), "M", 0L, PageRequest.of(0, 10));
        compareProduct(savedProduct, findProductByCategory.getContent().get(0));
        compareProduct(product2, findProductByCategory.getContent().get(1));
        compareProduct(product3, findProductByCategory.getContent().get(2));
        compareProduct(product4, findProductByCategory.getContent().get(3));
        compareProduct(product5, findProductByCategory.getContent().get(4));
    }

    @Test
    void productWithCategoryOfMall() {
        Page<ProductPreviewDto> findProductByCategoryOfMall =
                productService.productWithCategoryOfMall(mall.getId(), topCategory.getId(), "M", 0L, PageRequest.of(0, 10));
        compareProduct(savedProduct, findProductByCategoryOfMall.getContent().get(0));
        compareProduct(product2, findProductByCategoryOfMall.getContent().get(1));
        compareProduct(product3, findProductByCategoryOfMall.getContent().get(2));
        compareProduct(product4, findProductByCategoryOfMall.getContent().get(3));
        compareProduct(product5, findProductByCategoryOfMall.getContent().get(4));
    }

    @Test
    void multipleProductCountWithCategory() {
        Product topProduct2 = productService.save(new Product(
                new ProductDetailDto(10000, "tp2", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        Product topProduct3 = productService.save(new Product(
                new ProductDetailDto(10000, "tp3", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        Product bottomProduct1 = productService.save(new Product(
                new ProductDetailDto(10000, "bp1", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall, descImgs));
        Product bottomProduct2 = productService.save(new Product(
                new ProductDetailDto(10000, "bp2", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall, descImgs));
        Product bottomProduct3 = productService.save(new Product(
                new ProductDetailDto(10000, "bp3", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall, descImgs));
        Product bottomProduct4 = productService.save(new Product(
                new ProductDetailDto(10000, "bp4", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall, descImgs));

        List<ProductCategoryDto> findProductCountsOnCategory = productService.multipleProductCountWithCategory();
        assertThat(findProductCountsOnCategory.get(0).getCount()).isEqualTo(7);
        assertThat(findProductCountsOnCategory.get(1).getCount()).isEqualTo(4);
    }

    @Test
    void productCountWithCategoryOfMall() {
        Product topProduct2 = productService.save(new Product(
                new ProductDetailDto(10000, "tp2", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        Product topProduct3 = productService.save(new Product(
                new ProductDetailDto(10000, "tp3", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));

        Mall mall2 = mallService.save(new MallDto(2L, "testMall2", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        Product bottomProduct1 = productService.save(new Product(
                new ProductDetailDto(10000, "bp1", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall2", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall2, descImgs));
        Product bottomProduct2 = productService.save(new Product(
                new ProductDetailDto(10000, "bp2", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall2", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall2, descImgs));
        Product bottomProduct3 = productService.save(new Product(
                new ProductDetailDto(10000, "bp3", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall2", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall2, descImgs));
        Product bottomProduct4 = productService.save(new Product(
                new ProductDetailDto(10000, "bp4", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall2", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall2, descImgs));

        List<ProductCategoryDto> findProductCountsOnCategoryOfMall = productService.productCountWithCategoryOfMall(mall.getId());
        assertThat(findProductCountsOnCategoryOfMall.get(0).getCount()).isEqualTo(7);
        assertThat(findProductCountsOnCategoryOfMall.get(1).getCount()).isEqualTo(0);

        List<ProductCategoryDto> findProductCountsOnCategoryOfMall2 = productService.productCountWithCategoryOfMall(mall2.getId());
        assertThat(findProductCountsOnCategoryOfMall2.get(0).getCount()).isEqualTo(0);
        assertThat(findProductCountsOnCategoryOfMall2.get(1).getCount()).isEqualTo(4);
    }

    @Test
    void recommendProduct() {
        List<Long> productIds = new ArrayList<>(){{
            add(savedProduct.getId());
            add(product2.getId());
            add(product3.getId());
            add(product4.getId());
            add(product5.getId());
        }};

        //preview
        List<ProductPreviewDto> recommendedProductsPreview = productService.recommendProduct(productIds, true);
        assertThat(recommendedProductsPreview.size()).isEqualTo(4);
        compareProduct(savedProduct, recommendedProductsPreview.get(0));
        compareProduct(product2, recommendedProductsPreview.get(1));
        compareProduct(product3, recommendedProductsPreview.get(2));
        compareProduct(product4, recommendedProductsPreview.get(3));

        //not preview
        List<ProductPreviewDto> recommendedProducts = productService.recommendProduct(productIds, false);
        assertThat(recommendedProducts.size()).isEqualTo(5);
        compareProduct(savedProduct, recommendedProducts.get(0));
        compareProduct(product2, recommendedProducts.get(1));
        compareProduct(product3, recommendedProducts.get(2));
        compareProduct(product4, recommendedProducts.get(3));
        compareProduct(product5, recommendedProducts.get(4));
    }

    @Test
    void topProductDetail() {
        TopProductDto topProductDto = productService.topProductDetail(savedProduct.getId());
        assertThat(topProductDto.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(topProductDto.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(topProductDto.getProductGender()).isEqualTo(savedProduct.getGender());
        assertThat(topProductDto.getPrice()).isEqualTo(savedProduct.getPrice());

        Mall savedMall = savedProduct.getMall();
        assertThat(topProductDto.getMallName()).isEqualTo(savedMall.getName());
        assertThat(topProductDto.getMallUrl()).isEqualTo(savedMall.getUrl());
        assertThat(topProductDto.getMallImage()).isEqualTo(savedMall.getImage());

        assertThat(topProductDto.getCategory()).isEqualTo(savedProduct.getCategory().getName());
    }

    @Test
    void bottomProductDetail() {
        Product bottomProduct = productService.save(new Product(
                new ProductDetailDto(10000, "bp1", "M", 0,
                        "image.jpg", "bottom", "pants",
                        "testMall", null, null, null, null, descImgsStr),
                bottomCategory, bottomSubCategory, mall, descImgs));

        BottomProductDto bottomProductDto = productService.bottomProductDetail(bottomProduct.getId());
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
        productService.updateView(savedProduct.getId());
        Product findProduct = productService.findById(savedProduct.getId());
        assertThat(findProduct.getView()).isEqualTo(1);
    }

    @Test
    void recentRecommendationTest() {
        productService.saveRecentRecommendation(user.getId(), savedProduct.getId());
        productService.saveRecentRecommendation(user.getId(), product2.getId());
        productService.saveRecentRecommendation(user.getId(), product3.getId());
        productService.saveRecentRecommendation(user.getId(), product4.getId());
        productService.saveRecentRecommendation(user.getId(), product5.getId());

        List<Product> products = productService.productWithRecentRecommendation(user.getId());
        assertThat(products.size()).isEqualTo(5);
        compareProduct(savedProduct, products.get(0));
        compareProduct(product2, products.get(1));
        compareProduct(product3, products.get(2));
        compareProduct(product4, products.get(3));
        compareProduct(product5, products.get(4));
    }

    @Test
    void userRecommendationTest() {
        productService.saveUserRecommendation(user.getId(), savedProduct.getId());
        productService.saveUserRecommendation(user.getId(), product2.getId());
        productService.saveUserRecommendation(user.getId(), product3.getId());
        productService.saveUserRecommendation(user.getId(), product4.getId());
        productService.saveUserRecommendation(user.getId(), product5.getId());

        List<Product> products = productService.productWithUserRecommendation(user.getId());
        assertThat(products.size()).isEqualTo(5);
        compareProduct(savedProduct, products.get(0));
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

    private static void compareProduct(Product savedProduct, ProductPreviewDto productPreviewDto) {
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