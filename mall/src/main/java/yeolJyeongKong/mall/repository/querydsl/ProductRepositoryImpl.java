package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QSize.size;
import static yeolJyeongKong.mall.domain.entity.QTop.top;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

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
                .leftJoin(product.category, QCategory.category)
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
                .leftJoin(product.category, QCategory.category)
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
    public Page<ProductPreviewDto> productWithFavorite(Long userId, Pageable pageable) {

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
                .leftJoin(product.user, user)
                .leftJoin(product.mall, mall)
                .where(
                        userIdEq(userId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.user, user)
                .leftJoin(product.mall, mall)
                .where(
                        userIdEq(userId)
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
                .leftJoin(product.category, QCategory.category)
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
                .leftJoin(product.category, QCategory.category)
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
                .select(user.count())
                .from(user)
                .where(user.products.any().id.eq(productId))
                .fetchOne();

        Tuple tuple = queryFactory
                .select(product.count(), user.gender, user.ageRange)
                .from(product)
                .leftJoin(product.user, user)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender, user.ageRange)
                .orderBy(product.count().desc())
                .limit(1)
                .fetchOne();

        Tuple popular = Optional.ofNullable(tuple)
                .orElseThrow(() -> new NullPointerException("popular information doesn't exist"));

        String gender = popular.get(user.gender);
        Integer ageRange = popular.get(user.ageRange);

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

        return new TopProductDto(favoriteCount, productInfo, gender, ageRange, topDtos);
    }

    @Override
    public BottomProductDto bottomProductDetail(Long productId) {
        Long favoriteCount = queryFactory
                .select(user.count())
                .from(user)
                .where(user.products.any().id.eq(productId))
                .fetchOne();

        Tuple tuple = queryFactory
                .select(product.count(), user.gender, user.ageRange)
                .from(product)
                .leftJoin(product.user, user)
                .where(
                        productIdEq(productId)
                )
                .groupBy(user.gender, user.ageRange)
                .orderBy(product.count().desc())
                .limit(1)
                .fetchOne();

        Tuple popular = Optional.ofNullable(tuple)
                .orElseThrow(() -> new NullPointerException("popular information doesn't exist"));

        String gender = popular.get(user.gender);
        Integer ageRange = popular.get(user.ageRange);

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

        return new BottomProductDto(favoriteCount, productInfo, gender, ageRange, bottomDtos);
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
