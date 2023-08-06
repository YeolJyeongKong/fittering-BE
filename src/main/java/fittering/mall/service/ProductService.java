package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.*;
import fittering.mall.domain.mapper.CategoryMapper;
import fittering.mall.domain.mapper.SizeMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.RestPage;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PREVIEW_PRODUCT_COUNT = 4;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MallRepository mallRepository;
    private final RecentRecommendationRepository recentRecommendationRepository;
    private final UserRecommendationRepository userRecommendationRepository;
    private final DescriptionImageRepository descriptionImageRepository;
    private final SubCategoryRepository subCategoryRepository;

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
    public RestPage<ResponseProductPreviewDto> productWithCategory(Long categoryId, String gender, Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.productWithCategory(null, categoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "Product", key = "'1_' + #subCategoryId + '_' + #gender + '_' + #filterId + '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithSubCategory(Long subCategoryId, String gender, Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.productWithSubCategory(null, subCategoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + #categoryId + '_' + #gender + '_' + #filterId +  '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithCategoryOfMall(Long mallId, Long categoryId, String gender,
                                                             Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.productWithCategory(mallId, categoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "MallProduct", key = "'sub_' + #mallId + '_' + #subCategoryId + '_' + #gender + '_' + #filterId +  '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithSubCategoryOfMall(Long mallId, Long subCategoryId, String gender,
                                                                            Long filterId, Pageable pageable) {
        return new RestPage<>(productRepository.productWithSubCategory(mallId, subCategoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "Product", key = "'count'")
    public List<ResponseProductCategoryDto> multipleProductCountWithCategory() {

        List<ResponseProductCategoryDto> result = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    category.getName(),
                    productRepository.productCountWithCategory(category.getId())
            );
            result.add(categoryDto);
        });

        subCategoryRepository.findAll().forEach(subCategory -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    subCategory.getName(),
                    productRepository.productCountWithSubCategory(subCategory.getId())
            );
            result.add(categoryDto);
        });

        return result;
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + 'count'")
    public List<ResponseProductCategoryDto> productCountWithCategoryOfMall(Long mallId) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        List<ResponseProductCategoryDto> result = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    category.getName(),
                    productRepository.productCountWithCategoryOfMall(mall.getName(), category.getId())
            );
            result.add(categoryDto);
        });

        subCategoryRepository.findAll().forEach(subCategory -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    subCategory.getName(),
                    productRepository.productCountWithSubCategoryOfMall(mall.getName(), subCategory.getId())
            );
            result.add(categoryDto);
        });

        return result;
    }

    public List<ResponseProductPreviewDto> recommendProduct(List<Long> productIds, boolean preview) {

        List<ResponseProductPreviewDto> result = new ArrayList<>();

        for (int i = 0; i < productIds.size(); i++) {
            if (preview && i >= MAX_PREVIEW_PRODUCT_COUNT) break;
            Long productId = productIds.get(i);
            result.add(productRepository.productById(productId));
        }

        return result;
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseOuterDto outerProductDetail(Long productId) {
        return SizeMapper.INSTANCE.toResponseOuterDto(productRepository.outerProductDetail(productId));
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseTopDto topProductDetail(Long productId) {
        return SizeMapper.INSTANCE.toResponseTopDto(productRepository.topProductDetail(productId));
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseDressDto dressProductDetail(Long productId) {
        return SizeMapper.INSTANCE.toResponseDressDto(productRepository.dressProductDetail(productId));
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseBottomDto bottomProductDetail(Long productId) {
        return SizeMapper.INSTANCE.toResponseBottomDto(productRepository.bottomProductDetail(productId));
    }

    @Transactional
    public void updateView(Long productId) {
        Product product = findById(productId);
        product.updateView();
        product.updateTimeView();
    }

    @Transactional
    public RecentRecommendation saveRecentRecommendation(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Product product = findById(productId);
        return recentRecommendationRepository.save(RecentRecommendation.builder()
                                                    .user(user)
                                                    .product(product)
                                                    .build());
    }

    @Transactional
    public UserRecommendation saveUserRecommendation(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Product product = findById(productId);
        return userRecommendationRepository.save(UserRecommendation.builder()
                                                    .user(user)
                                                    .product(product)
                                                    .updatedAt(LocalDateTime.now())
                                                    .build());
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
    public List<DescriptionImage> saveDescriptionImages(List<String> descriptionImages, Product product) {
        List<DescriptionImage> result = new ArrayList<>();
        descriptionImages.forEach(descriptionImageUrl -> {
            DescriptionImage descriptionImage = DescriptionImage.builder()
                                                    .url(descriptionImageUrl)
                                                    .product(product)
                                                    .build();
            result.add(descriptionImageRepository.save(descriptionImage));
        });
        return result;
    }
}
