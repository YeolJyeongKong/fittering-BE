package fittering.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fittering.mall.domain.entity.Category;
import fittering.mall.domain.entity.SubCategory;
import fittering.mall.repository.CategoryRepository;
import fittering.mall.repository.SubCategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public Category save(String categoryName) {
        return categoryRepository.save(Category.builder()
                                        .name(categoryName)
                                        .build());
    }

    public SubCategory saveSubCategory(String categoryName, String subCategoryName) {
        Category category = findByName(categoryName);
        return subCategoryRepository.save(SubCategory.builder()
                                            .category(category)
                                            .name(subCategoryName)
                                            .build());
    }

    public Category findByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NoResultException("category dosen't exist"));
    }

    public SubCategory findByNameOfSubCategory(String subCategoryName) {
        return subCategoryRepository.findByName(subCategoryName)
                .orElseThrow(() -> new NoResultException("category dosen't exist"));
    }
}
