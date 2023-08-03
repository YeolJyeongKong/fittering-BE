package fittering.mall.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import fittering.mall.domain.dto.*;
import fittering.mall.domain.entity.*;

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

    private JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ProductPreviewDto productById(Long productId) {
        return queryFactory
                .select(new QProductPreviewDto(
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
    public Page<ProductPreviewDto> productWithCategory(Long mallId, Long categoryId, String gender,
                                                       Long filterId, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
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
    public Page<ProductPreviewDto> productWithSubCategory(Long mallId, Long subCategoryId, String gender,
                                                          Long filterId, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
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
    public Page<ProductPreviewDto> searchProduct(String productName, String gender, Long filterId, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
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
                .from(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.sizes, size)
                .leftJoin(size.outer, outer)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<Size> sizes = productInfo.getSizes();
        List<OuterDto> outerDtos = new ArrayList<>();
        for (Size size : sizes) {
            outerDtos.add(new OuterDto(size));
        }

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
                .limit(1)
                .fetchOne();

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if(optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return new OuterProductDto(favoriteCount, productInfo, gender, ageRange, outerDtos);
        }

        return new OuterProductDto(favoriteCount, productInfo, "", null, outerDtos);
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
                .from(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.sizes, size)
                .leftJoin(size.top, top)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<Size> sizes = productInfo.getSizes();
        List<TopDto> topDtos = new ArrayList<>();
        for (Size size : sizes) {
            topDtos.add(new TopDto(size));
        }

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
                .limit(1)
                .fetchOne();

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if(optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return new TopProductDto(favoriteCount, productInfo, gender, ageRange, topDtos);
        }

        return new TopProductDto(favoriteCount, productInfo, "", null, topDtos);
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
                .from(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.sizes, size)
                .leftJoin(size.dress, dress)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<Size> sizes = productInfo.getSizes();
        List<DressDto> dressDtos = new ArrayList<>();
        for (Size size : sizes) {
            dressDtos.add(new DressDto(size));
        }

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
                .limit(1)
                .fetchOne();

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if(optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return new DressProductDto(favoriteCount, productInfo, gender, ageRange, dressDtos);
        }

        return new DressProductDto(favoriteCount, productInfo, "", null, dressDtos);
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
                .limit(1)
                .fetchOne();

        Product productInfo = queryFactory
                .selectFrom(product)
                .from(product)
                .leftJoin(product.mall, mall)
                .leftJoin(product.category, category)
                .leftJoin(product.sizes, size)
                .leftJoin(size.bottom, bottom)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

        List<Size> sizes = productInfo.getSizes();
        List<BottomDto> bottomDtos = new ArrayList<>();
        for (Size size : sizes) {
            bottomDtos.add(new BottomDto(size));
        }

        Optional<Tuple> optionalPopular = Optional.ofNullable(tuple);

        if(optionalPopular.isPresent()) {
            Tuple popular = optionalPopular.get();
            String gender = popular.get(user.gender);
            Integer ageRange = popular.get(user.ageRange);
            return new BottomProductDto(favoriteCount, productInfo, gender, ageRange, bottomDtos);
        }

        return new BottomProductDto(favoriteCount, productInfo, "", null, bottomDtos);
    }

    @Override
    public List<Product> findByIds(List<Long> productIds) {
        List<Product> result = new ArrayList<>();

        for (Long productId : productIds) {
            Product product = queryFactory
                    .selectFrom(QProduct.product)
                    .where(
                            productIdEq(productId)
                    )
                    .fetchOne();
            result.add(product);
        }

        return result;
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

    public OrderSpecifier<? extends Number> filter(Long filterId) {
        if(filterId == 0) {
            return product.id.asc();
        }
        if(filterId == 1) {
            return product.view.desc();
        }
        if(filterId == 2) {
            return product.price.asc();
        }
        return product.price.desc();
    }
}
