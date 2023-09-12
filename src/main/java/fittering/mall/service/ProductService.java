package fittering.mall.service;

import fittering.mall.config.kafka.domain.dto.CrawledMallDto;
import fittering.mall.config.kafka.domain.dto.CrawledProductDto;
import fittering.mall.config.kafka.domain.dto.CrawledSizeDto;
import fittering.mall.domain.dto.controller.response.*;
import fittering.mall.domain.mapper.CategoryMapper;
import fittering.mall.domain.mapper.MallMapper;
import fittering.mall.domain.mapper.ProductMapper;
import fittering.mall.domain.mapper.SizeMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.RestPage;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fittering.mall.domain.entity.Product.*;

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
    private final ProductDescriptionRepository productDescriptionRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SizeRepository sizeRepository;
    private final OuterRepository outerRepository;
    private final TopRepository topRepository;
    private final DressRepository dressRepository;
    private final BottomRepository bottomRepository;
    private final FavoriteRepository favoriteRepository;
    private final RedisService redisService;
    private final S3Service s3Service;

    @Value("${cloud.aws.cloudfront.url}")
    private String CLOUDFRONT_URL;

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

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
    public ResponseOuterDto outerProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        return SizeMapper.INSTANCE.toResponseOuterDto(productRepository.outerProductDetail(productId), productDescriptions, isFavorite);
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseTopDto topProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        return SizeMapper.INSTANCE.toResponseTopDto(productRepository.topProductDetail(productId), productDescriptions, isFavorite);
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseDressDto dressProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        return SizeMapper.INSTANCE.toResponseDressDto(productRepository.dressProductDetail(productId), productDescriptions, isFavorite);
    }

    @Cacheable(value = "ProductDetail", key = "#productId")
    public ResponseBottomDto bottomProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        return SizeMapper.INSTANCE.toResponseBottomDto(productRepository.bottomProductDetail(productId), productDescriptions, isFavorite);
    }

    public void updateView(Long productId) {
        redisService.updateViewOfProduct(productId);
        redisService.updateTimeViewOfProduct(productId);
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
    public List<ProductDescription> saveProductDescriptions(List<String> productDescriptions, Product product) {
        List<ProductDescription> result = new ArrayList<>();
        productDescriptions.forEach(productDescriptionUrl -> {
            ProductDescription productDescription = ProductDescription.builder()
                                                    .url(CLOUDFRONT_URL + productDescriptionUrl)
                                                    .product(product)
                                                    .build();
            result.add(productDescriptionRepository.save(productDescription));
        });
        return result;
    }


    public List<ResponseProductDescriptionDto> getProductDescriptions(Long productId) {
        List<ResponseProductDescriptionDto> responseProductDescriptionDtos = new ArrayList<>();
        productDescriptionRepository.getProductDescrtiptions(productId).forEach(productDescription ->
                responseProductDescriptionDtos.add(ProductMapper.INSTANCE.toResponseProductDescriptionDto(productDescription))
        );
        return responseProductDescriptionDtos;
    }

    @Transactional
    public void initializeTimeView() {
        productRepository.findAll().forEach(Product::initializeTimeView);
    }

    public List<ResponseProductPreviewDto> productsOfTimeRank(String gender) {
        return productRepository.timeRank(gender);
    }

    public LocalDateTime productsOfMaxUpdatedAt() {
        return productRepository.maxUpdatedAt();
    }

    public void updateCrawledProducts(CrawledProductDto productDto,
                                      CrawledMallDto mallDto,
                                      List<CrawledSizeDto> sizeDtos,
                                      List<String> imagePaths) {
        Optional<Product> optionalProduct = productRepository.findByName(productDto.getName());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            LocalDateTime updatedAt = getLocalDateTimeFromString(productDto.getUpdated_at());
            product.updateInfo(productDto.getPrice(), productDto.getDisabled(), updatedAt);
            return;
        }

        Category category = categoryRepository.findById(productDto.getCategory_id())
                .orElseThrow(() -> new NoResultException("category doesn't exist"));
        SubCategory subCategory = subCategoryRepository.findById(productDto.getSub_category_id())
                .orElseThrow(() -> new NoResultException("sub_category doesn't exist"));
        Mall mall = mallRepository.findById(productDto.getMall_id())
                .orElse(mallRepository.save(MallMapper.INSTANCE.toMall(mallDto)));

        String thumbnail = CLOUDFRONT_URL + imagePaths.get(0);
        Product product = save(ProductMapper.INSTANCE.toProduct(
                productDto, thumbnail, 0, 0, category, subCategory, mall));

        imagePaths.set(0, productDto.getDescription_path());
        saveProductDescriptions(imagePaths, product);

        imagePaths.forEach(imagePath -> {
            try {
                s3Service.moveS3ObjectToServerBucket(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        if (productDto.getType().equals(OUTER)) {
            getSizesOfOuter(sizeDtos, product);
            return;
        }

        if (productDto.getType().equals(TOP)) {
            getSizesOfTop(sizeDtos, product);
            return;
        }

        if (productDto.getType().equals(DRESS)) {
            getSizesOfDress(sizeDtos, product);
            return;
        }

        getSizesOfBottom(sizeDtos, product);
    }

    private static LocalDateTime getLocalDateTimeFromString(String updatedAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(updatedAt, formatter);
    }

    private void getSizesOfOuter(List<CrawledSizeDto> outerSizeDtos, Product product) {
        outerSizeDtos.forEach(outerSizeDto -> {
            Outer outer = outerRepository.save(SizeMapper.INSTANCE.toOuter(outerSizeDto));
            sizeRepository.save(Size.builder()
                    .name(outerSizeDto.getName())
                    .outer(outer)
                    .product(product)
                    .build());
        });
    }

    private void getSizesOfTop(List<CrawledSizeDto> topSizeDtos, Product product) {
        topSizeDtos.forEach(topSizeDto -> {
            Top top = topRepository.save(SizeMapper.INSTANCE.toTop(topSizeDto));
            sizeRepository.save(Size.builder()
                    .name(topSizeDto.getName())
                    .top(top)
                    .product(product)
                    .build());
        });
    }

    private void getSizesOfDress(List<CrawledSizeDto> dressSizeDtos, Product product) {
        dressSizeDtos.forEach(dressSizeDto -> {
            Dress dress = dressRepository.save(SizeMapper.INSTANCE.toDress(dressSizeDto));
            sizeRepository.save(Size.builder()
                    .name(dressSizeDto.getName())
                    .dress(dress)
                    .product(product)
                    .build());
        });
    }

    private void getSizesOfBottom(List<CrawledSizeDto> bottomSizeDtos, Product product) {
        bottomSizeDtos.forEach(bottomSizeDto -> {
            Bottom bottom = bottomRepository.save(SizeMapper.INSTANCE.toBottom(bottomSizeDto));
            sizeRepository.save(Size.builder()
                    .name(bottomSizeDto.getName())
                    .bottom(bottom)
                    .product(product)
                    .build());
        });
    }
}
