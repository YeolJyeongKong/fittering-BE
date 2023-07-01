package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.QProductPreviewDto;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QCategory.category;
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductPreviewDto> productCategoryPreview(String categoryName, String gender, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        product.mall.name.as("mallName")
                ))
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.mall, mall)
                .where(
                        categoryNameEq(categoryName),
                        genderEq(gender)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .where(
                        categoryNameEq(categoryName),
                        genderEq(gender)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public Page<ProductPreviewDto> productLikePreview(Long userId, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        product.mall.name.as("mallName")
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
                .from(category)
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
    public Page<ProductPreviewDto> productSearchPreview(String productName, String gender, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        product.mall.name.as("mallName")
                ))
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.mall, mall)
                .where(
                        productNameCotnains(productName),
                        genderEq(gender)
                )
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

    public BooleanExpression categoryNameEq(String categoryName) {
        return StringUtils.hasText(categoryName) ? category.name.eq(categoryName) : null;
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    /**
     * @param gender
     * null == 전체
     * 이외 성별 구분
     */
    public BooleanExpression genderEq(String gender) {
        return StringUtils.hasText(gender) ? product.gender.eq(gender) : Expressions.asBoolean(true).isTrue();
    }

    public BooleanExpression productNameCotnains(String productName) {
        return StringUtils.hasText(productName) ? product.name.contains(productName) : null;
    }
}
