package fittering.mall.service;

import fittering.mall.config.kafka.domain.dto.CrawledMallDto;
import fittering.mall.config.kafka.domain.dto.CrawledProductDto;
import fittering.mall.config.kafka.domain.dto.CrawledSizeDto;
import fittering.mall.controller.dto.response.*;
import fittering.mall.domain.collection.Products;
import fittering.mall.domain.mapper.CategoryMapper;
import fittering.mall.domain.mapper.MallMapper;
import fittering.mall.domain.mapper.ProductMapper;
import fittering.mall.domain.mapper.SizeMapper;
import fittering.mall.service.dto.ProductParamDto;
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
    private static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);

    @Value("${cloud.aws.cloudfront.url}")
    private String CLOUDFRONT_URL;

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

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));
    }

    @Cacheable(value = "Product", key = "'0_' + #categoryId + '_' + #gender + '_' + #filterId + '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithCategory(ProductParamDto productParamDto, Pageable pageable) {
        Long categoryId = productParamDto.getCategoryId();
        String gender = productParamDto.getGender();
        Long filterId = productParamDto.getFilterId();
        return new RestPage<>(productRepository.productWithCategory(null, categoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "Product", key = "'1_' + #subCategoryId + '_' + #gender + '_' + #filterId + '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithSubCategory(ProductParamDto productParamDto, Pageable pageable) {
        Long subCategoryId = productParamDto.getSubCategoryId();
        String gender = productParamDto.getGender();
        Long filterId = productParamDto.getFilterId();
        return new RestPage<>(productRepository.productWithSubCategory(null, subCategoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + #categoryId + '_' + #gender + '_' + #filterId +  '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithCategoryOfMall(ProductParamDto productParamDto, Pageable pageable) {
        Long mallId = productParamDto.getMallId();
        Long categoryId = productParamDto.getCategoryId();
        String gender = productParamDto.getGender();
        Long filterId = productParamDto.getFilterId();
        return new RestPage<>(productRepository.productWithCategory(mallId, categoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "MallProduct", key = "'sub_' + #mallId + '_' + #subCategoryId + '_' + #gender + '_' + #filterId +  '_' + #pageable.pageNumber")
    public RestPage<ResponseProductPreviewDto> productWithSubCategoryOfMall(ProductParamDto productParamDto, Pageable pageable) {
        Long mallId = productParamDto.getMallId();
        Long subCategoryId = productParamDto.getSubCategoryId();
        String gender = productParamDto.getGender();
        Long filterId = productParamDto.getFilterId();
        return new RestPage<>(productRepository.productWithSubCategory(mallId, subCategoryId, gender, filterId, pageable));
    }

    @Cacheable(value = "Product", key = "'count'")
    public List<ResponseProductCategoryDto> multipleProductCountWithCategory() {
        List<ResponseProductCategoryDto> productCategoryDtos = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    category.getName(),
                    productRepository.productCountWithCategory(category.getId())
            );
            productCategoryDtos.add(categoryDto);
        });

        subCategoryRepository.findAll().forEach(subCategory -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    subCategory.getName(),
                    productRepository.productCountWithSubCategory(subCategory.getId())
            );
            productCategoryDtos.add(categoryDto);
        });

        return productCategoryDtos;
    }

    @Cacheable(value = "MallProduct", key = "#mallId + '_' + 'count'")
    public List<ResponseProductCategoryDto> productCountWithCategoryOfMall(Long mallId) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        List<ResponseProductCategoryDto> productCategoryDtos = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    category.getName(),
                    productRepository.productCountWithCategoryOfMall(mall.getName(), category.getId())
            );
            productCategoryDtos.add(categoryDto);
        });

        subCategoryRepository.findAll().forEach(subCategory -> {
            ResponseProductCategoryDto categoryDto = CategoryMapper.INSTANCE.toResponseProductCategoryDto(
                    subCategory.getName(),
                    productRepository.productCountWithSubCategoryOfMall(mall.getName(), subCategory.getId())
            );
            productCategoryDtos.add(categoryDto);
        });

        return productCategoryDtos;
    }

    public List<ResponseProductPreviewDto> recommendProduct(List<Long> productIds, boolean preview) {
        List<ResponseProductPreviewDto> productDtos = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            if (preview && i >= MAX_PREVIEW_PRODUCT_COUNT) break;
            Long productId = productIds.get(i);
            productDtos.add(productRepository.productById(productId));
        }
        return productDtos;
    }

    public ResponseOuterDto outerProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        List<Integer> popularAgeRangePercents = productRepository.findPopularAgeRangePercents(productId);
        List<Integer> popularGenderPercents = productRepository.findPopularGenderPercents(productId);
        return SizeMapper.INSTANCE.toResponseOuterDto(productRepository.outerProductDetail(productId), productDescriptions,
                isFavorite, popularAgeRangePercents, popularGenderPercents);
    }

    public ResponseTopDto topProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        List<Integer> popularAgeRangePercents = productRepository.findPopularAgeRangePercents(productId);
        List<Integer> popularGenderPercents = productRepository.findPopularGenderPercents(productId);
        return SizeMapper.INSTANCE.toResponseTopDto(productRepository.topProductDetail(productId), productDescriptions,
                isFavorite, popularAgeRangePercents, popularGenderPercents);
    }

    public ResponseDressDto dressProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        List<Integer> popularAgeRangePercents = productRepository.findPopularAgeRangePercents(productId);
        List<Integer> popularGenderPercents = productRepository.findPopularGenderPercents(productId);
        return SizeMapper.INSTANCE.toResponseDressDto(productRepository.dressProductDetail(productId), productDescriptions,
                isFavorite, popularAgeRangePercents, popularGenderPercents);
    }

    public ResponseBottomDto bottomProductDetail(Long userId, Long productId) {
        List<ResponseProductDescriptionDto> productDescriptions = getProductDescriptions(productId);
        Boolean isFavorite = favoriteRepository.isUserFavoriteProduct(userId, productId);
        List<Integer> popularAgeRangePercents = productRepository.findPopularAgeRangePercents(productId);
        List<Integer> popularGenderPercents = productRepository.findPopularGenderPercents(productId);
        return SizeMapper.INSTANCE.toResponseBottomDto(productRepository.bottomProductDetail(productId), productDescriptions,
                isFavorite, popularAgeRangePercents, popularGenderPercents);
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

    public Products productWithRecentRecommendation(Long userId) {
        Products recommendedProducts = new Products();
        recentRecommendationRepository.findByUserId(userId).forEach(recentRecommendation ->
                recommendedProducts.add(recentRecommendation.getProduct()));
        return recommendedProducts;
    }

    public Products productWithUserRecommendation(Long userId) {
        Products recommendedProducts = new Products();
        userRecommendationRepository.findByUserId(userId).forEach(userRecommendation ->
                recommendedProducts.add(userRecommendation.getProduct()));
        return recommendedProducts;
    }

    @Transactional
    public List<ProductDescription> saveProductDescriptions(List<String> productDescriptions, Product product) {
        List<ProductDescription> savedProductDescriptions = new ArrayList<>();
        productDescriptions.forEach(productDescriptionUrl -> {
            ProductDescription productDescription = ProductDescription.builder()
                                                    .url(CLOUDFRONT_URL + productDescriptionUrl)
                                                    .product(product)
                                                    .build();
            savedProductDescriptions.add(productDescriptionRepository.save(productDescription));
        });
        return savedProductDescriptions;
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
        return productRepository.maxUpdatedAt().orElse(DEFAULT_TIME);
    }

    @Transactional
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
        Optional<Mall> optionalMall = mallRepository.findByName(mallDto.getName());

        if (optionalMall.isPresent()) {
            Mall mall = optionalMall.get();
            synchronizeProduct(productDto, sizeDtos, imagePaths, category, subCategory, mall);
            return;
        }

        CrawledMallDto newMallDto = CrawledMallDto.builder()
                .mall_id(mallDto.getMall_id())
                .name(mallDto.getName())
                .url(mallDto.getUrl())
                .description(mallDto.getDescription())
                .image(CLOUDFRONT_URL + mallDto.getImage())
                .build();
        Mall mall = mallRepository.save(MallMapper.INSTANCE.toMall(newMallDto));
        synchronizeProduct(productDto, sizeDtos, imagePaths, category, subCategory, mall);
    }

    private void synchronizeProduct(CrawledProductDto productDto,
                                    List<CrawledSizeDto> sizeDtos,
                                    List<String> imagePaths,
                                    Category category,
                                    SubCategory subCategory,
                                    Mall mall) {
        String thumbnail = CLOUDFRONT_URL + imagePaths.get(0);
        Product product = save(ProductMapper.INSTANCE.toProduct(
                productDto, thumbnail, 0, 0, category, subCategory, mall));

        imagePaths.set(0, productDto.getDescription_path());
        saveProductDescriptions(imagePaths, product);

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
