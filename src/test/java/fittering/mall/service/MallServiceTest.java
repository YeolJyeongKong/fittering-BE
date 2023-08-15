package fittering.mall.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.entity.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MallServiceTest {

    @Autowired
    MallService mallService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void End() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
    }

    @Test
    @DisplayName("쇼핑몰 테스트")
    void mallTest() {
        Mall mall1 = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        Mall mall2 = mallService.save(new MallDto(2L, "testMall2", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        Mall mall3 = mallService.save(new MallDto(3L, "testMall3", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));

        Mall findMall1 = mallService.findByName("testMall1");
        Mall findMall2 = mallService.findByName("testMall2");
        Mall findMall3 = mallService.findByName("testMall3");

        checkMall(mall1, findMall1);
        checkMall(mall2, findMall2);
        checkMall(mall3, findMall3);

        Category category = categoryService.save("top");
        SubCategory subCategory = categoryService.saveSubCategory("top", "shirt");
        List<String> descImgsStr = List.of("descImage.jpg");
        Product product = productService.save(Product.builder()
                .price(10000)
                .name("tp1")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/1")
                .category(category)
                .subCategory(subCategory)
                .mall(mall1)
                .build());
        List<ProductDescription> descImgs = List.of(new ProductDescription(descImgsStr.get(0), product));

        mallService.addProduct(mall1.getName(), product.getId());
        mallService.findProducts("testMall1").forEach(productDto -> {
            assertThat(productDto.getProductId()).isEqualTo(product.getId());
            assertThat(productDto.getProductImage()).isEqualTo(product.getImage());
            assertThat(productDto.getProductName()).isEqualTo(product.getName());
            assertThat(productDto.getPrice()).isEqualTo(product.getPrice());
            assertThat(productDto.getMallName()).isEqualTo(product.getMall().getName());
            assertThat(productDto.getMallUrl()).isEqualTo(product.getMall().getUrl());
        });
    }

    private static void checkMall(Mall savedMall, Mall findMall) {
        assertThat(savedMall.getName()).isEqualTo(findMall.getName());
        assertThat(savedMall.getUrl()).isEqualTo(findMall.getUrl());
        assertThat(savedMall.getImage()).isEqualTo(findMall.getImage());
        assertThat(savedMall.getDescription()).isEqualTo(findMall.getDescription());
    }
}