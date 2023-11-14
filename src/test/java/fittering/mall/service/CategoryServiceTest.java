package fittering.mall.service;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.SubCategory;
import fittering.mall.repository.CategoryRepository;
import fittering.mall.repository.SubCategoryRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.entity.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("카테고리 정보를 저장한다.")
    @Test
    void saveCategory() {
        //given
        String categoryName = "outer";

        //when
        Category savedCategory = categoryService.save(categoryName);

        //then
        assertThat(savedCategory.getName()).isEqualTo(categoryName);
    }

    @DisplayName("서브 카테고리 정보를 저장한다.")
    @Test
    void saveSubCategory() {
        //given
        String categoryName = "outer";
        String subCategoryName = "hoodedzip";

        categoryRepository.save(Category.builder()
                .name(categoryName)
                .build());

        //when
        SubCategory savedSubCategory = categoryService.saveSubCategory(categoryName, subCategoryName);

        //then
        assertThat(savedSubCategory.getName()).isEqualTo(subCategoryName);
        assertThat(savedSubCategory.getCategory().getName()).isEqualTo(categoryName);
    }

    @DisplayName("저장된 카테고리를 이름으로 조회할 수 있다.")
    @Test
    void findCategoryByName() {
        //given
        String categoryName = "outer";

        categoryRepository.save(Category.builder()
                .name(categoryName)
                .build());

        //when
        Category targetCategory = categoryService.findByName(categoryName);

        //then
        assertThat(targetCategory.getName()).isEqualTo(categoryName);
    }

    @DisplayName("저장되지 않은 카테고리는 이름으로 조회할 수 없다.")
    @Test
    void findNotSavedCategoryByName() {
        //given
        String categoryName = "outer";

        //when //then
        assertThatThrownBy(() -> categoryService.findByName(categoryName))
                .isInstanceOf(NoResultException.class)
                        .hasMessage("category dosen't exist");
    }

    @DisplayName("저장된 서브 카테고리를 이름으로 조회할 수 있다.")
    @Test
    void findSubCategoryByName() {
        //given
        String categoryName = "outer";
        String subCategoryName = "hoodedzip";

        Category savedCategory = categoryRepository.save(Category.builder()
                .name(categoryName)
                .build());
        SubCategory savedSubCategory = subCategoryRepository.save(SubCategory.builder()
                .category(savedCategory)
                .name(subCategoryName)
                .build());

        //when
        SubCategory targetSubCategory = categoryService.findByNameOfSubCategory(subCategoryName);

        //then
        assertThat(targetSubCategory.getName()).isEqualTo(subCategoryName);
    }

    @DisplayName("저장되지 않은 서브 카테고리는 이름으로 조회할 수 없다.")
    @Test
    void findNotSavedSubCategoryByName() {
        //given
        String subCategoryName = "hoodedzip";

        //when //then
        assertThatThrownBy(() -> categoryService.findByNameOfSubCategory(subCategoryName))
                .isInstanceOf(NoResultException.class)
                .hasMessage("category dosen't exist");
    }
}