package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.SubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class SubCategoryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @DisplayName("이름으로 서브 카테고리를 조회할 수 있다.")
    @Test
    void findSubCategoryByName() {
        //given
        SubCategory savedSubCategory = subCategoryRepository.save(SubCategory.builder()
                .name("top")
                .build());

        //when
        Optional<SubCategory> target = subCategoryRepository.findByName("top");

        //then
        assertThat(target).isNotEmpty();
        assertThat(target.get())
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        savedSubCategory.getId(), savedSubCategory.getName()
                );
    }
}