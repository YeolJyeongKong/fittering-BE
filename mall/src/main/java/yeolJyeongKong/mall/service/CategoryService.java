package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.entity.Category;
import yeolJyeongKong.mall.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void save(String categoryName) {
        categoryRepository.save(new Category(categoryName));
    }

    public Category findByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NoResultException("category dosen't exist"));
    }
}
