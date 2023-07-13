package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.BottomProductDto;
import yeolJyeongKong.mall.domain.dto.ProductCategoryDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.TopProductDto;
import yeolJyeongKong.mall.domain.entity.*;
import yeolJyeongKong.mall.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MallRepository mallRepository;
    private final RecentRecommendationRepository recentRecommendationRepository;
    private final UserRecommendationRepository userRecommendationRepository;

    @Cacheable(value = "Product", key = "#productId")
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));
    }

    @Cacheable(value = "Product", key = "#categoryId + '_' + #gender")
    public Page<ProductPreviewDto> productWithCategory(Long categoryId, String gender, Pageable pageable) {
        return productRepository.productWithCategory(null, categoryId, gender, pageable);
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + #categoryId + '_' + #gender")
    public Page<ProductPreviewDto> productWithCategoryOfMall(Long mallId, Long categoryId,
                                                             String gender, Pageable pageable) {
        return productRepository.productWithCategory(mallId, categoryId, gender, pageable);
    }

    @Cacheable(value = "FavoriteProductOfUser", key = "#userId")
    public Page<ProductPreviewDto> productWithUserFavorite(Long userId, Pageable pageable) {
        return productRepository.productWithFavorite(userId, pageable);
    }

    @Cacheable(value = "Product", key = "'count'")
    public List<ProductCategoryDto> multipleProductCountWithCategory() {

        List<Category> categories = categoryRepository.findAll();
        List<ProductCategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(new ProductCategoryDto(category.getName(),
                    productRepository.productCountWithCategory(category.getId())));
        }

        return result;
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + 'count'")
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

    @Cacheable(value = "TopProduct", key = "#productId")
    public TopProductDto topProductDetail(Long productId) {
        return productRepository.topProductDetail(productId);
    }

    @Cacheable(value = "BottomProduct", key = "#productId")
    public BottomProductDto bottomProductDetail(Long productId) {
        return productRepository.bottomProductDetail(productId);
    }

    @Transactional
    public void updateView(Long productId) {
        Product product = findById(productId);
        product.updateView();
    }

    @Transactional
    public RecentRecommendation saveRecentRecommendation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));

        RecentRecommendation recentRecommendation = new RecentRecommendation(user);
        recentRecommendationRepository.save(recentRecommendation);
        return recentRecommendation;
    }

    @Transactional
    public void updateRecentRecommendation(RecentRecommendation recentRecommendation, List<Long> productIds) {
        List<Product> products = productRepository.findByIds(productIds);
        recentRecommendation.update(products);
    }

    @Transactional
    public UserRecommendation saveUserRecommendation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));

        UserRecommendation userRecommendation = new UserRecommendation(user);
        userRecommendationRepository.save(userRecommendation);
        return userRecommendation;
    }

    @Transactional
    public void updateUserRecommendation(UserRecommendation userRecommendation, List<Long> productIds) {
        List<Product> products = productRepository.findByIds(productIds);
        userRecommendation.update(products);
    }

    @Cacheable(value = "ProductWithRecentRecommendation", key = "#userId")
    public List<Product> productWithRecentRecommendation(Long userId) {
        return recentRecommendationRepository.findByUserId(userId)
                .map(RecentRecommendation::getProducts)
                .orElse(new ArrayList<>());
    }

    @Cacheable(value = "ProductWithUserRecommendation", key = "#userId")
    public List<Product> productWithUserRecommendation(Long userId) {
        return userRecommendationRepository.findByUserId(userId)
                .map(UserRecommendation::getProducts)
                .orElse(new ArrayList<>());
    }
}
