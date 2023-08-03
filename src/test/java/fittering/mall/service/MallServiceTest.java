package fittering.mall.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import fittering.mall.domain.dto.MallDto;
import fittering.mall.domain.dto.ProductDetailDto;
import fittering.mall.domain.entity.*;

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

        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        Product product = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                category, subCategory, mall1, descImgs));
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