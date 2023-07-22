package yeolJyeongKong.mall.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.*;

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
        mall = mallService.save(new MallDto("testMall", "testMall.com",
                "image.jpg", "desc", new ArrayList<>()));
        mall2 = mallService.save(new MallDto("testMall2", "testMall.com",
                "image.jpg", "desc", new ArrayList<>()));
        savedProduct = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "desc", "top",
                        "testMall", null, null),
                topCategory, mall));
        user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        product2 = productService.save(new Product(
                new ProductDetailDto(10000, "tp2", "M", 0,
                        "image.jpg", "desc", "top",
                        "testMall", null, null),
                topCategory, mall));
        product3 = productService.save(new Product(
                new ProductDetailDto(10000, "tp3", "M", 0,
                        "image.jpg", "desc", "top",
                        "testMall", null, null),
                topCategory, mall));
        product4 = productService.save(new Product(
                new ProductDetailDto(10000, "tp4", "M", 0,
                        "image.jpg", "desc", "top",
                        "testMall2", null, null),
                topCategory, mall2));
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

        assertThat(mallDtos.get(0).getName()).isEqualTo(rank1.getMall().getName());
        assertThat(mallDtos.get(0).getUrl()).isEqualTo(rank1.getMall().getUrl());
        assertThat(mallDtos.get(0).getImage()).isEqualTo(rank1.getMall().getImage());
        assertThat(mallDtos.get(0).getDescription()).isEqualTo(rank1.getMall().getDescription());

        List<MallRankProductDto> productDto = mallDtos.get(0).getProducts();
        assertThat(productDto.get(0).getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productDto.get(0).getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productDto.get(1).getProductId()).isEqualTo(product2.getId());
        assertThat(productDto.get(1).getProductImage()).isEqualTo(product2.getImage());
        assertThat(productDto.get(2).getProductId()).isEqualTo(product3.getId());
        assertThat(productDto.get(2).getProductImage()).isEqualTo(product3.getImage());

        assertThat(mallDtos.get(1).getName()).isEqualTo(rank2.getMall().getName());
        assertThat(mallDtos.get(1).getUrl()).isEqualTo(rank2.getMall().getUrl());
        assertThat(mallDtos.get(1).getImage()).isEqualTo(rank2.getMall().getImage());
        assertThat(mallDtos.get(1).getDescription()).isEqualTo(rank2.getMall().getDescription());

        productDto = mallDtos.get(1).getProducts();
        assertThat(productDto.get(0).getProductId()).isEqualTo(product4.getId());
        assertThat(productDto.get(0).getProductImage()).isEqualTo(product4.getImage());

        rankService.updateView(user.getId(), savedProduct.getId());
        rankService.updateView(user.getId(), product4.getId());

        rank1 = rankService.findById(rank1.getId());
        rank2 = rankService.findById(rank2.getId());
        assertThat(rank1.getView()).isEqualTo(1L);
        assertThat(rank2.getView()).isEqualTo(1L);
    }
}