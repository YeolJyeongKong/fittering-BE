package yeolJyeongKong.mall.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.ProductDetailDto;
import yeolJyeongKong.mall.domain.entity.Category;
import yeolJyeongKong.mall.domain.entity.DescriptionImage;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.Product;

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

    @Test
    @DisplayName("쇼핑몰 테스트")
    void mallTest() {
        Mall mall1 = mallService.save(new MallDto("testMall1", "testMall.com", "image.jpg", "desc", new ArrayList<>()));
        Mall mall2 = mallService.save(new MallDto("testMall2", "testMall.com", "image.jpg", "desc", new ArrayList<>()));
        Mall mall3 = mallService.save(new MallDto("testMall3", "testMall.com", "image.jpg", "desc", new ArrayList<>()));

        Mall findMall1 = mallService.findByName("testMall1");
        Mall findMall2 = mallService.findByName("testMall2");
        Mall findMall3 = mallService.findByName("testMall3");

        checkMall(mall1, findMall1);
        checkMall(mall2, findMall2);
        checkMall(mall3, findMall3);

        Category category = categoryService.save("top");
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        Product product = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "top",
                        "testMall", null, null, descImgsStr),
                category, mall1, descImgs));
        mallService.addProduct("testMall1", product.getId());
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