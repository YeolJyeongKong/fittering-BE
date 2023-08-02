package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.RestPage;
import yeolJyeongKong.mall.domain.dto.*;
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
    private final DescriptionImageRepository descriptionImageRepository;

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Cacheable(value = "Product", key = "#productId")
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));
    }

    @Cacheable(value = "Product", key = "'0_' + #categoryId + '_' + #gender + '_' + #filterId + '_' + #pageable.pageNumber")
    public RestPage<ProductPreviewDto> productWithCategory(Long categoryId, String gender, Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.productWithCategory(null, categoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + #categoryId + '_' + #gender + '_' + #filterId +  '_' + #pageable.pageNumber")
    public RestPage<ProductPreviewDto> productWithCategoryOfMall(Long mallId, Long categoryId, String gender,
                                                             Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.productWithCategory(mallId, categoryId, gender, filterId, pageable));
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

        for (int i = 0; i < productIds.size(); i++) {
            if (preview && i >= 4) break;
            Long productId = productIds.get(i);
            result.add(productRepository.productById(productId));
        }

        return result;
    }

    @Cacheable(value = "OuterProduct", key = "#productId")
    public OuterProductDto outerProductDetail(Long productId) {
        return productRepository.outerProductDetail(productId);
    }

    @Cacheable(value = "TopProduct", key = "#productId")
    public TopProductDto topProductDetail(Long productId) {
        return productRepository.topProductDetail(productId);
    }

    @Cacheable(value = "DressProduct", key = "#productId")
    public DressProductDto dressProductDetail(Long productId) {
        return productRepository.dressProductDetail(productId);
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
    public RecentRecommendation saveRecentRecommendation(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Product product = findById(productId);
        return recentRecommendationRepository.save(new RecentRecommendation(user, product));
    }

    @Transactional
    public UserRecommendation saveUserRecommendation(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Product product = findById(productId);
        return userRecommendationRepository.save(new UserRecommendation(user, product));
    }

    public List<Product> productWithRecentRecommendation(Long userId) {
        List<Product> result = new ArrayList<>();
        recentRecommendationRepository.findByUserId(userId).forEach(recentRecommendation ->
                result.add(recentRecommendation.getProduct()));
        return result;
    }

    public List<Product> productWithUserRecommendation(Long userId) {
        List<Product> result = new ArrayList<>();
        userRecommendationRepository.findByUserId(userId).forEach(userRecommendation ->
                result.add(userRecommendation.getProduct()));
        return result;
    }

    @Transactional
    public List<DescriptionImage> saveDescriptionImages(List<String> descriptionImages) {
        List<DescriptionImage> result = new ArrayList<>();
        descriptionImages.forEach(descriptionImageUrl -> {
            DescriptionImage descriptionImage = new DescriptionImage(descriptionImageUrl);
            result.add(descriptionImageRepository.save(descriptionImage));
        });
        return result;
    }
}
