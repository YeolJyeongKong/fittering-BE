package fittering.mall.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.domain.dto.controller.response.QResponseProductPreviewDto;
import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.dto.service.*;
import fittering.mall.domain.mapper.SizeMapper;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import fittering.mall.domain.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fittering.mall.domain.entity.QBottom.bottom;
import static fittering.mall.domain.entity.QCategory.category;
import static fittering.mall.domain.entity.QDress.dress;
import static fittering.mall.domain.entity.QFavorite.favorite;
import static fittering.mall.domain.entity.QMall.mall;
import static fittering.mall.domain.entity.QOuter.outer;
import static fittering.mall.domain.entity.QProduct.product;
import static fittering.mall.domain.entity.QRecent.recent;
import static fittering.mall.domain.entity.QRecentRecommendation.recentRecommendation;
import static fittering.mall.domain.entity.QSize.size;
import static fittering.mall.domain.entity.QSubCategory.subCategory;
import static fittering.mall.domain.entity.QTop.top;
import static fittering.mall.domain.entity.QUser.user;
import static fittering.mall.domain.entity.QUserRecommendation.userRecommendation;
import static fittering.mall.repository.querydsl.EqualMethod.*;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private static final int INDEX_DESC = 0;
    private static final int VIEW_DESC = 1;
    private static final int PRICE_ASC = 2;
    private static final int MOST_POPULAR_TARGET_COUNT = 1;
    private static final int TIME_RANK_PRODUCT_COUNT = 18;
    private JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ResponseProductPreviewDto productById(Long productId) {
        return queryFactory
                .select(new QResponseProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(product)
                .leftJoin(product.mall, mall)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();
    }

    @Override
    public Page<ResponseProductPreviewDto> productWithCategory(Long mallId, Long categoryId, String gender,
                                                               Long filterId, Pageable pageable) {

        List<ResponseProductPreviewDto> content = queryFactory
                .select(new QResponseProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.mall, mall)
                .where(
                        categoryIdEq(categoryId),
                        genderEq(gender),
                        mallIdEq(mallId)
                )
                .orderBy(filter(filterId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        categoryIdEq(categoryId),
                        genderEq(gender),
                        mallIdEq(mallId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        Long count = nullableCount != null ? nullableCount : 0L;

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public Page<ResponseProductPreviewDto> productWithSubCategory(Long mallId, Long subCategoryId, String gender,
                                                          Long filterId, Pageable pageable) {

        List<ResponseProductPreviewDto> content = queryFactory
                .select(new QResponseProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(product)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.mall, mall)
                .where(
                        subCategoryIdEq(subCategoryId),
                        genderEq(gender),
                        mallIdEq(mallId)
                )
                .orderBy(filter(filterId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.subCategory, subCategory)
                .where(
                        subCategoryIdEq(subCategoryId),
                        genderEq(gender),
                        mallIdEq(mallId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        Long count = nullableCount != null ? nullableCount : 0L;

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public Page<ResponseProductPreviewDto> searchProduct(String productName, String gender, Long filterId, Pageable pageable) {

        List<ResponseProductPreviewDto> content = queryFactory
                .select(new QResponseProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.mall, mall)
                .where(
                        productNameContains(productName),
                        genderEq(gender)
                )
                .orderBy(filter(filterId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        productNameContains(productName),
                        genderEq(gender)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        Long count = nullableCount != null ? nullableCount : 0L;

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public List<String> relatedSearch(String keyword) {
        return queryFactory
                .select(product.name)
                .from(product)
                .where(
                        product.name.contains(keyword)
                )
                .fetch();
    }

    @Override
    public Long productCountWithCategory(Long categoryId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        categoryIdEq(categoryId)
                )
                .fetchOne();

        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public Long productCountWithSubCategory(Long subCategoryId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.subCategory, subCategory)
                .where(
                        subCategoryIdEq(subCategoryId)
                )
                .fetchOne();

        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public Long productCountWithCategoryOfMall(String mallName, Long categoryId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.mall, mall)
                .where(
                        categoryIdEq(categoryId),
                        mallNameEq(mallName)
                )
                .fetchOne();

        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public Long productCountWithSubCategoryOfMall(String mallName, Long subCategoryId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.mall, mall)
                .where(
                        subCategoryIdEq(subCategoryId),
                        mallNameEq(mallName)
                )
                .fetchOne();

        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public OuterProductDto outerProductDetail(Long productId) {
        Long nullableFavoriteCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        Long favoriteCount = nullableFavoriteCount != null ? nullableFavoriteCount : 0L;

        Product productInfo = queryFactory
                .selectFrom(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.sizes, size)
                .leftJoin(size.outer, outer)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<OuterDto> outerDtos = new ArrayList<>();
        productInfo.getSizes().forEach(size -> outerDtos.add(OuterDto.builder()
                                                                .name(size.getName())
                                                                .full(size.getOuter().getFull())
                                                                .shoulder(size.getOuter().getShoulder())
                                                                .chest(size.getOuter().getChest())
                                                                .sleeve(size.getOuter().getSleeve())
                                                                .build()));

        Tuple tuple = queryFactory
                .select(product.count(), user.gender, user.ageRange)
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender, user.ageRange)
                .orderBy(favorite.count().desc())
                .limit(MOST_POPULAR_TARGET_COUNT)
                .fetchOne();

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if (optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return SizeMapper.INSTANCE.toOuterProductDto(favoriteCount, productInfo, gender, ageRange, outerDtos);
        }

        return SizeMapper.INSTANCE.toOuterProductDto(favoriteCount, productInfo, "", null, outerDtos);
    }

    @Override
    public TopProductDto topProductDetail(Long productId) {
        Long nullableFavoriteCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        Long favoriteCount = nullableFavoriteCount != null ? nullableFavoriteCount : 0L;

        Product productInfo = queryFactory
                .selectFrom(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.sizes, size)
                .leftJoin(size.top, top)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<TopDto> topDtos = new ArrayList<>();
        productInfo.getSizes().forEach(size -> topDtos.add(TopDto.builder()
                                                            .name(size.getName())
                                                            .full(size.getTop().getFull())
                                                            .shoulder(size.getTop().getShoulder())
                                                            .chest(size.getTop().getChest())
                                                            .sleeve(size.getTop().getSleeve())
                                                            .build()));

        Tuple tuple = queryFactory
                .select(product.count(), user.gender, user.ageRange)
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender, user.ageRange)
                .orderBy(favorite.count().desc())
                .limit(MOST_POPULAR_TARGET_COUNT)
                .fetchOne();

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if (optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return SizeMapper.INSTANCE.toTopProductDto(favoriteCount, productInfo, gender, ageRange, topDtos);
        }

        return SizeMapper.INSTANCE.toTopProductDto(favoriteCount, productInfo, "", null, topDtos);
    }

    @Override
    public DressProductDto dressProductDetail(Long productId) {
        Long nullableFavoriteCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        Long favoriteCount = nullableFavoriteCount != null ? nullableFavoriteCount : 0L;

        Product productInfo = queryFactory
                .selectFrom(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.sizes, size)
                .leftJoin(size.dress, dress)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<DressDto> dressDtos = new ArrayList<>();
        productInfo.getSizes().forEach(size -> dressDtos.add(DressDto.builder()
                                                                .name(size.getName())
                                                                .full(size.getDress().getFull())
                                                                .shoulder(size.getDress().getShoulder())
                                                                .chest(size.getDress().getChest())
                                                                .waist(size.getDress().getWaist())
                                                                .armHall(size.getDress().getArmHall())
                                                                .hip(size.getDress().getHip())
                                                                .sleeve(size.getDress().getSleeve())
                                                                .sleeveWidth(size.getDress().getSleeveWidth())
                                                                .bottomWidth(size.getDress().getBottomWidth())
                                                                .build()));

        Tuple tuple = queryFactory
                .select(product.count(), user.gender, user.ageRange)
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender, user.ageRange)
                .orderBy(favorite.count().desc())
                .limit(MOST_POPULAR_TARGET_COUNT)
                .fetchOne();

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if (optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return SizeMapper.INSTANCE.toDressProductDto(favoriteCount, productInfo, gender, ageRange, dressDtos);
        }

        return SizeMapper.INSTANCE.toDressProductDto(favoriteCount, productInfo, "", null, dressDtos);
    }

    @Override
    public BottomProductDto bottomProductDetail(Long productId) {
        Long nullableFavoriteCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        Long favoriteCount = nullableFavoriteCount != null ? nullableFavoriteCount : 0L;

        Tuple tuple = queryFactory
                .select(product.count(), user.gender, user.ageRange)
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender, user.ageRange)
                .orderBy(favorite.count().desc())
                .limit(MOST_POPULAR_TARGET_COUNT)
                .fetchOne();

        Product productInfo = queryFactory
                .selectFrom(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.subCategory, subCategory)
                .leftJoin(product.sizes, size)
                .leftJoin(size.bottom, bottom)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<BottomDto> bottomDtos = new ArrayList<>();
        productInfo.getSizes().forEach(size -> bottomDtos.add(BottomDto.builder()
                                                                .name(size.getName())
                                                                .full(size.getBottom().getFull())
                                                                .waist(size.getBottom().getWaist())
                                                                .thigh(size.getBottom().getThigh())
                                                                .rise(size.getBottom().getRise())
                                                                .bottomWidth(size.getBottom().getBottomWidth())
                                                                .hipWidth(size.getBottom().getHipWidth())
                                                                .build()));

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if (optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return SizeMapper.INSTANCE.toBottomProductDto(favoriteCount, productInfo, gender, ageRange, bottomDtos);
        }

        return SizeMapper.INSTANCE.toBottomProductDto(favoriteCount, productInfo, "", null, bottomDtos);
    }

    @Override
    public Long findFavoriteCount(Long favoriteId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.favorites, favorite)
                .where(
                        favoriteIdEq(favoriteId)
                )
                .fetchOne();
        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public Long findRecentCount(Long recentId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.recents, recent)
                .where(
                        recentIdEq(recentId)
                )
                .fetchOne();
        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public Long findRecentRecommendation(Long recentRecommendationId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.recentRecommendations, recentRecommendation)
                .where(
                        recentRecommendationIdEq(recentRecommendationId)
                )
                .fetchOne();
        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public Long findUserRecommendation(Long userRecommendationId) {
        Long nullableCount = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.userRecommendations, userRecommendation)
                .where(
                        userRecommendationIdEq(userRecommendationId)
                )
                .fetchOne();
        return nullableCount != null ? nullableCount : 0L;
    }

    @Override
    public List<Integer> findPopularAgeRangePercents(Long productId) {
        List<Long> ageRangeFavoriteCounts = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.ageRange)
                .fetch();

        List<Integer> popularAgeRangePercentages = new ArrayList<>();

        long totalFavoriteCount = ageRangeFavoriteCounts.stream()
                .mapToLong(favoriteCount -> favoriteCount != null ? favoriteCount : 0L)
                .sum();

        for (Long favoriteCount : ageRangeFavoriteCounts) {
            double percentage = (double) favoriteCount / totalFavoriteCount * 100;
            popularAgeRangePercentages.add((int) percentage);
        }

        return popularAgeRangePercentages;
    }

    @Override
    public List<Integer> findPopularGenderPercents(Long productId) {
        List<Long> genderFavoriteCounts = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.user, user)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender)
                .fetch();

        List<Integer> popularGenderPercentages = new ArrayList<>();

        long totalFavoriteCount = genderFavoriteCounts.stream()
                .mapToLong(favoriteCount -> favoriteCount != null ? favoriteCount : 0L)
                .sum();

        for (Long favoriteCount : genderFavoriteCounts) {
            double percentage = (double) favoriteCount / totalFavoriteCount * 100;
            popularGenderPercentages.add((int) percentage);
        }

        return popularGenderPercentages;
    }

    @Override
    public List<ResponseProductPreviewDto> timeRank(String gender) {
        return queryFactory
                .select(new QResponseProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(product)
                .where(
                        genderEq(gender)
                )
                .leftJoin(product.mall, mall)
                .orderBy(product.timeView.desc())
                .limit(TIME_RANK_PRODUCT_COUNT)
                .fetch();
    }

    @Override
    public LocalDateTime maxUpdatedAt() {
        return queryFactory
                .select(product.updatedAt.max())
                .from(product)
                .fetchOne();
    }

    public OrderSpecifier<? extends Number> filter(Long filterId) {
        if (filterId == INDEX_DESC) {
            return product.id.desc();
        }
        if (filterId == VIEW_DESC) {
            return product.view.desc();
        }
        if (filterId == PRICE_ASC) {
            return product.price.asc();
        }
        return product.price.desc();
    }
}
