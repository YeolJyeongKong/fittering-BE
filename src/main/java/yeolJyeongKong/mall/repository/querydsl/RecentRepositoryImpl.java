package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.QProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.QMall;
import yeolJyeongKong.mall.domain.entity.Recent;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QCategory.category;
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QRecent.recent;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class RecentRepositoryImpl implements RecentRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public RecentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Recent> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(recent)
                .leftJoin(recent.user, user)
                .leftJoin(recent.product, product)
                .where(
                        userIdEq(userId)
                )
                .fetch();
    }

    /**
     * 6개 노출
     * 일주일에 한 번 초기화
     */
    @Override
    public List<ProductPreviewDto> recentProductPreview(Long userId) {
        return queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(recent)
                .leftJoin(recent.user, user)
                .leftJoin(recent.product, product)
                .leftJoin(product.mall, mall)
                .where(
                        userIdEq(userId)
                )
                .orderBy(recent.timestamp.desc())
                .limit(6)
                .fetch();
    }

    /**
     * 전체 노출
     * 일주일에 한 번 초기화
     */
    @Override
    public Page<ProductPreviewDto> recentProduct(Long userId, Pageable pageable) {
        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(recent)
                .leftJoin(recent.user, user)
                .leftJoin(recent.product, product)
                .leftJoin(product.mall, mall)
                .where(
                        userIdEq(userId)
                )
                .orderBy(recent.timestamp.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long nullableCount = queryFactory
                .select(recent.count())
                .from(recent)
                .leftJoin(recent.user, user)
                .where(
                        userIdEq(userId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();

        Long count = nullableCount != null ? nullableCount : 0L;

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
