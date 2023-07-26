package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.metamodel.model.domain.TupleType;
import org.hibernate.sql.results.internal.TupleImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static yeolJyeongKong.mall.domain.entity.QBottom.bottom;
import static yeolJyeongKong.mall.domain.entity.QCategory.category;
import static yeolJyeongKong.mall.domain.entity.QFavorite.favorite;
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QRecent.recent;
import static yeolJyeongKong.mall.domain.entity.QRecentRecommendation.recentRecommendation;
import static yeolJyeongKong.mall.domain.entity.QSize.size;
import static yeolJyeongKong.mall.domain.entity.QTop.top;
import static yeolJyeongKong.mall.domain.entity.QUser.user;
import static yeolJyeongKong.mall.domain.entity.QUserRecommendation.userRecommendation;

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

        Long count = queryFactory
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
                        productNameCotnains(productName),
                        genderEq(gender)
                )
                .orderBy(filter(filterId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        productNameCotnains(productName),
                        genderEq(gender)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public Long productCountWithCategory(Long categoryId) {
        return queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        categoryIdEq(categoryId)
                )
                .fetchOne();
    }

    @Override
    public Long productCountWithCategoryOfMall(String mallName, Long categoryId) {
        return queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.mall, mall)
                .where(
                        categoryIdEq(categoryId),
                        mallNameEq(mallName)
                )
                .fetchOne();
    }

    @Override
    public TopProductDto topProductDetail(Long productId) {
        Long favoriteCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

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
    public BottomProductDto bottomProductDetail(Long productId) {
        Long favoriteCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .leftJoin(favorite.product, product)
                .where(
                        productIdEq(productId)
                )
                .fetchOne();

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
        return queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.favorites, favorite)
                .where(
                        favoriteIdEq(favoriteId)
                )
                .fetchOne();
    }

    @Override
    public Long findRecentCount(Long recentId) {
        return queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.recent, recent)
                .where(
                        recentIdEq(recentId)
                )
                .fetchOne();
    }

    @Override
    public Long findRecentRecommendation(Long recentRecommendationId) {
        return queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.recentRecommendation, recentRecommendation)
                .where(
                        recentRecommendationIdEq(recentRecommendationId)
                )
                .fetchOne();
    }

    @Override
    public Long findUserRecommendation(Long userRecommendationId) {
        return queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.userRecommendation, userRecommendation)
                .where(
                        userRecommendationIdEq(userRecommendationId)
                )
                .fetchOne();
    }

    public BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? category.id.eq(categoryId) : null;
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    /**
     * @param gender
     * null     : 전체
     * not null : 이외 성별 구분
     */
    public BooleanExpression genderEq(String gender) {
        return StringUtils.hasText(gender) ? product.gender.eq(gender) : Expressions.asBoolean(true).isTrue();
    }

    public BooleanExpression productNameCotnains(String productName) {
        return StringUtils.hasText(productName) ? product.name.contains(productName) : null;
    }

    public BooleanExpression mallNameEq(String mallName) {
        return StringUtils.hasText(mallName) ? mall.name.eq(mallName) : null;
    }

    /**
     * @param mallId
     * null     : 전체
     * not null : 쇼핑몰 구분
     */
    public BooleanExpression mallIdEq(Long mallId) {
        return mallId != null ? mall.id.eq(mallId) : Expressions.asBoolean(true).isTrue();
    }

    public BooleanExpression productIdEq(Long productId) {
        return productId != null ? product.id.eq(productId) : null;
    }

    public BooleanExpression favoriteIdEq(Long favoriteId) {
        return favoriteId != null ? favorite.id.eq(favoriteId) : null;
    }

    public BooleanExpression recentIdEq(Long recentId) {
        return recentId != null ? recent.id.eq(recentId) : null;
    }

    public BooleanExpression recentRecommendationIdEq(Long recentRecommendationId) {
        return recentRecommendationId != null ? recentRecommendation.id.eq(recentRecommendationId) : null;
    }

    public BooleanExpression userRecommendationIdEq(Long userRecommendationId) {
        return userRecommendationId != null ? userRecommendation.id.eq(userRecommendationId) : null;
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
