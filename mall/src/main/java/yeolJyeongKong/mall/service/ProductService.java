package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.ProductCategoryDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.Category;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.repository.CategoryRepository;
import yeolJyeongKong.mall.repository.MallRepository;
import yeolJyeongKong.mall.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MallRepository mallRepository;

    public Page<ProductPreviewDto> productWithCategory(String category, String gender, Pageable pageable) {
        return productRepository.productWithCategory(category, gender, pageable);
    }

    public Page<ProductPreviewDto> productWithUserFavorite(Long userId, Pageable pageable) {
        return productRepository.productWithFavorite(userId, pageable);
    }

    public List<ProductCategoryDto> multipleProductCountWithCategory() {

        List<Category> categories = categoryRepository.findAll();
        List<ProductCategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(new ProductCategoryDto(category.getName(),
                                              productRepository.productCountWithCategory(category.getName())));
        }

        return result;
    }

    public List<ProductCategoryDto> productCountWithCategoryOfMall(Long mallId) {

        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        List<Category> categories = categoryRepository.findAll();
        List<ProductCategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(new ProductCategoryDto(category.getName(),
                    productRepository.productCountWithCategoryOfMall(mall.getName(), category.getName())));
        }

        return result;
    }
}
