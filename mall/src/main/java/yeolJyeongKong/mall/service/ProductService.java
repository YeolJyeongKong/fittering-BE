package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.BottomProductDto;
import yeolJyeongKong.mall.domain.dto.ProductCategoryDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.TopProductDto;
import yeolJyeongKong.mall.domain.entity.Category;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.Product;
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

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));
    }

    public Page<ProductPreviewDto> productWithCategory(Long categoryId, String gender, Pageable pageable) {
        return productRepository.productWithCategory(null, categoryId, gender, pageable);
    }

    public Page<ProductPreviewDto> productWithCategoryOfMall(Long mallId, Long categoryId,
                                                             String gender, Pageable pageable) {
        return productRepository.productWithCategory(mallId, categoryId, gender, pageable);
    }

    public Page<ProductPreviewDto> productWithUserFavorite(Long userId, Pageable pageable) {
        return productRepository.productWithFavorite(userId, pageable);
    }

    public List<ProductCategoryDto> multipleProductCountWithCategory() {

        List<Category> categories = categoryRepository.findAll();
        List<ProductCategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(new ProductCategoryDto(category.getName(),
                    productRepository.productCountWithCategory(category.getId())));
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
                    productRepository.productCountWithCategoryOfMall(mall.getName(), category.getId())));
        }

        return result;
    }

    public List<ProductPreviewDto> recommendProduct(List<Long> productIds, boolean preview) {

        List<ProductPreviewDto> result = new ArrayList<>();

        for(int i = 0; i < productIds.size(); i++) {
            if (preview && i >= 4) break;

            Long productId = productIds.get(i);
            result.add(productRepository.productById(productId));
        }

        return result;
    }

    public TopProductDto topProductDetail(Long productId) {
        return productRepository.topProductDetail(productId);
    }

    public BottomProductDto bottomProductDetail(Long productId) {
        return productRepository.bottomProductDetail(productId);
    }

    @Transactional
    public void updateView(Long productId) {
        Product product = findById(productId);
        product.updateView();
    }
}
