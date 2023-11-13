package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class CategoryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("이름으로 카테고리를 조회할 수 있다.")
    @Test
    void findByName() {
        //given
        Category savedCategory = categoryRepository.save(Category.builder()
                .name("상의")
                .build());

        //when
        Optional<Category> target = categoryRepository.findByName(savedCategory.getName());

        //then
        assertThat(target).isNotNull();
        assertThat(target.get().getName()).isEqualTo(savedCategory.getName());
    }

}