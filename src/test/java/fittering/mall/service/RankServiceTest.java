package fittering.mall.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.dto.*;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class RankServiceTest {

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
    private SubCategory topSubCategory;
    private Mall mall;
    private Mall mall2;
    private Product savedProduct;
    private Product product2;
    private Product product3;
    private Product product4;
    private User user;

    @BeforeEach
    void setUp() {
        topCategory = categoryService.save("top");
        topSubCategory = categoryService.saveSubCategory("top", "shirt");
        mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        mall2 = mallService.save(new MallDto(2L, "testMall2", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        savedProduct = productService.save(new Product(
                new ProductDetailDto(10000, "A 티셔츠", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product2 = productService.save(new Product(
                new ProductDetailDto(10000, "A 셔츠", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product3 = productService.save(new Product(
                new ProductDetailDto(10000, "B 티셔츠", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));
        product4 = productService.save(new Product(
                new ProductDetailDto(10000, "ABC 스웨터", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall2", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall2, descImgs));
    }

    @Test
    void mallRank() {
        mallService.addProduct(mall.getName(), savedProduct.getId());
        mallService.addProduct(mall.getName(), product2.getId());
        mallService.addProduct(mall.getName(), product3.getId());
        mallService.addProduct(mall2.getName(), product4.getId());

        Rank rank1 = rankService.save(user.getId(), mall.getId());
        Rank rank2 = rankService.save(user.getId(), mall2.getId());

        List<MallDto> mallDtos = rankService.mallRank(user.getId());

        assertThat(mallDtos.get(0).getId()).isEqualTo(rank1.getMall().getId());
        assertThat(mallDtos.get(0).getName()).isEqualTo(rank1.getMall().getName());
        assertThat(mallDtos.get(0).getImage()).isEqualTo(rank1.getMall().getImage());
        assertThat(mallDtos.get(0).getView()).isEqualTo(rank1.getView());

        List<MallRankProductDto> productDto = mallDtos.get(0).getProducts();
        assertThat(productDto.get(0).getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productDto.get(0).getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productDto.get(1).getProductId()).isEqualTo(product2.getId());
        assertThat(productDto.get(1).getProductImage()).isEqualTo(product2.getImage());
        assertThat(productDto.get(2).getProductId()).isEqualTo(product3.getId());
        assertThat(productDto.get(2).getProductImage()).isEqualTo(product3.getImage());

        assertThat(mallDtos.get(1).getId()).isEqualTo(rank1.getMall().getId());
        assertThat(mallDtos.get(1).getName()).isEqualTo(rank1.getMall().getName());
        assertThat(mallDtos.get(1).getImage()).isEqualTo(rank1.getMall().getImage());
        assertThat(mallDtos.get(1).getView()).isEqualTo(rank1.getView());

        productDto = mallDtos.get(1).getProducts();
        assertThat(productDto.get(0).getProductId()).isEqualTo(product4.getId());
        assertThat(productDto.get(0).getProductImage()).isEqualTo(product4.getImage());

        rankService.updateViewOnProduct(user.getId(), savedProduct.getId());
        rankService.updateViewOnProduct(user.getId(), product4.getId());

        rank1 = rankService.findById(rank1.getId());
        rank2 = rankService.findById(rank2.getId());
        assertThat(rank1.getView()).isEqualTo(1L);
        assertThat(rank2.getView()).isEqualTo(1L);
    }
}