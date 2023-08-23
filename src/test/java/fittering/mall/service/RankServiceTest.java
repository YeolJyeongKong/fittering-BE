package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.ResponseMallWithProductDto;
import fittering.mall.domain.dto.controller.response.ResponseMallRankProductDto;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.dto.service.SignUpDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
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
                .name("tp1")
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
                .name("tp2")
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
                .name("tp3")
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
                .name("tp4")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/4")
                .category(topCategory)
                .subCategory(topSubCategory)
                .mall(mall2)
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
    void mallRank() {
        mallService.addProduct(mall.getName(), product.getId());
        mallService.addProduct(mall.getName(), product2.getId());
        mallService.addProduct(mall.getName(), product3.getId());
        mallService.addProduct(mall2.getName(), product4.getId());

        Rank rank1 = rankService.save(user.getId(), mall.getId());
        Rank rank2 = rankService.save(user.getId(), mall2.getId());

        List<ResponseMallWithProductDto> mallDtos = rankService.mallRank(user.getId());

        assertThat(mallDtos.get(0).getId()).isEqualTo(rank1.getMall().getId());
        assertThat(mallDtos.get(0).getName()).isEqualTo(rank1.getMall().getName());
        assertThat(mallDtos.get(0).getImage()).isEqualTo(rank1.getMall().getImage());
        assertThat(mallDtos.get(0).getView()).isEqualTo(rank1.getView());

        List<ResponseMallRankProductDto> productDto = mallDtos.get(0).getProducts();
        assertThat(productDto.get(0).getProductId()).isEqualTo(product.getId());
        assertThat(productDto.get(0).getProductImage()).isEqualTo(product.getImage());
        assertThat(productDto.get(1).getProductId()).isEqualTo(product2.getId());
        assertThat(productDto.get(1).getProductImage()).isEqualTo(product2.getImage());
        assertThat(productDto.get(2).getProductId()).isEqualTo(product3.getId());
        assertThat(productDto.get(2).getProductImage()).isEqualTo(product3.getImage());

        assertThat(mallDtos.get(1).getId()).isEqualTo(rank2.getMall().getId());
        assertThat(mallDtos.get(1).getName()).isEqualTo(rank2.getMall().getName());
        assertThat(mallDtos.get(1).getImage()).isEqualTo(rank2.getMall().getImage());
        assertThat(mallDtos.get(1).getView()).isEqualTo(rank2.getView());

        productDto = mallDtos.get(1).getProducts();
        assertThat(productDto.get(0).getProductId()).isEqualTo(product4.getId());
        assertThat(productDto.get(0).getProductImage()).isEqualTo(product4.getImage());

        rankService.updateViewOnMall(user.getId(), product.getMall().getId());
        rankService.updateViewOnMall(user.getId(), product4.getMall().getId());

        rank1 = rankService.findById(rank1.getId());
        rank2 = rankService.findById(rank2.getId());
        assertThat(rank1.getView()).isEqualTo(1L);
        assertThat(rank2.getView()).isEqualTo(1L);
    }
}