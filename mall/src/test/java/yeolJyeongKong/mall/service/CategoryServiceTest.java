package yeolJyeongKong.mall.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import yeolJyeongKong.mall.domain.entity.Category;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Test
    @DisplayName("카테고리 저장 및 이름으로 찾기")
    void categoryTest() {
        Category savedCategory = categoryService.save("카테고리1");
        Category findCategory = categoryService.findByName("카테고리1");
        assertThat(savedCategory).isEqualTo(findCategory);
    }
}