package yeolJyeongKong.mall.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class SizeServiceTest {

    @Autowired
    SizeService sizeService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    MallService mallService;
    @Autowired
    ProductService productService;

    @Test
    void saveTop() {
        Category category = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        Product product = productService.save(new Product(
                new ProductDetailDto(10000, "A 티셔츠", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                category, topSubCategory, mall, descImgs));

        TopDto topDto1 = new TopDto("S", 68.0, 50.0, 53.0, 24.0);
        TopDto topDto2 = new TopDto("M", 69.5, 51.5, 55.5, 25.0);
        TopDto topDto3 = new TopDto("L", 71.0, 53.0, 58.0, 26.0);

        Size topSize1 = sizeService.saveTop(topDto1, product);
        Size topSize2 = sizeService.saveTop(topDto2, product);
        Size topSize3 = sizeService.saveTop(topDto3, product);

        compareTopSize(topDto1, topSize1);
        compareTopSize(topDto2, topSize2);
        compareTopSize(topDto3, topSize3);
    }

    @Test
    void saveBottom() {
        Category category = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        Product product = productService.save(new Product(
                new ProductDetailDto(10000, "A 티셔츠", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                category, topSubCategory, mall, descImgs));

        BottomDto bottomDto1 = new BottomDto("S", 104.0, 37.5, 51.5, 33.8, 28.0, 26.0);
        BottomDto bottomDto2 = new BottomDto("M", 105.0, 40.0, 54.0, 35.0, 29.0, 27.0);
        BottomDto bottomDto3 = new BottomDto("L", 106.0, 42.5, 56.5, 36.2, 30.0, 28.0);

        Size bottomSize1 = sizeService.saveBottom(bottomDto1, product);
        Size bottomSize2 = sizeService.saveBottom(bottomDto2, product);
        Size bottomSize3 = sizeService.saveBottom(bottomDto3, product);

        compareBottomSize(bottomDto1, bottomSize1);
        compareBottomSize(bottomDto2, bottomSize2);
        compareBottomSize(bottomDto3, bottomSize3);
    }

    @Test
    void setProduct() {
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(new MallDto(1L, "testMall1", "test.com", "image.jpg", "desc", 0, new ArrayList<>()));
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        Product product = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "top", "shirt",
                        "testMall", null, null, null, null, descImgsStr),
                topCategory, topSubCategory, mall, descImgs));

        BottomDto bottomDto = new BottomDto("S", 104.0, 37.5, 51.5, 33.8, 28.0, 26.0);
        Size bottomSize = sizeService.saveBottom(bottomDto, product);

        sizeService.setProduct(bottomSize, product);

        assertEquals(bottomSize.getProduct(), product);
    }

    private static void compareTopSize(TopDto topDto, Size topSize) {
        assertEquals(topDto.getName(), topSize.getName());
        assertEquals(topDto.getFull(), topSize.getTop().getFull());
        assertEquals(topDto.getShoulder(), topSize.getTop().getShoulder());
        assertEquals(topDto.getChest(), topSize.getTop().getChest());
        assertEquals(topDto.getSleeve(), topSize.getTop().getSleeve());
    }

    private static void compareBottomSize(BottomDto bottomDto, Size bottomSize) {
        assertEquals(bottomDto.getName(), bottomSize.getName());
        assertEquals(bottomDto.getFull(), bottomSize.getBottom().getFull());
        assertEquals(bottomDto.getWaist(), bottomSize.getBottom().getWaist());
        assertEquals(bottomDto.getThigh(), bottomSize.getBottom().getThigh());
        assertEquals(bottomDto.getRise(), bottomSize.getBottom().getRise());
        assertEquals(bottomDto.getBottomWidth(), bottomSize.getBottom().getBottomWidth());
        assertEquals(bottomDto.getHipWidth(), bottomSize.getBottom().getHipWidth());
    }
}