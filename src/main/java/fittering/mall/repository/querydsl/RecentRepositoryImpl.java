package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.controller.dto.response.QResponseProductPreviewDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import fittering.mall.domain.entity.Recent;

import java.util.List;

import static fittering.mall.domain.entity.QMall.mall;
import static fittering.mall.domain.entity.QProduct.product;
import static fittering.mall.domain.entity.QRecent.recent;
import static fittering.mall.domain.entity.QUser.user;
import static fittering.mall.repository.querydsl.EqualMethod.productIdEq;
import static fittering.mall.repository.querydsl.EqualMethod.userIdEq;

public class RecentRepositoryImpl implements RecentRepositoryCustom {

    private static final int MAX_PRODUCT_PREVIEW_COUNT = 6;

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
    public List<ResponseProductPreviewDto> recentProductPreview(Long userId) {
        return queryFactory
                .select(new QResponseProductPreviewDto(
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
                .limit(MAX_PRODUCT_PREVIEW_COUNT)
                .fetch();
    }

    /**
     * 전체 노출
     * 일주일에 한 번 초기화
     */
    @Override
    public Page<ResponseProductPreviewDto> recentProduct(Long userId, Pageable pageable) {
        List<ResponseProductPreviewDto> content = queryFactory
                .select(new QResponseProductPreviewDto(
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

    @Override
    public void initializeRecents(Long userId) {
        queryFactory
                .delete(recent)
                .where(
                        userIdEq(userId)
                )
                .execute();
    }

    @Override
    public List<Long> recentProductIds(Long userId) {
        return queryFactory
                .select(product.id)
                .from(recent)
                .leftJoin(recent.user, user)
                .leftJoin(recent.product, product)
                .where(
                        userIdEq(userId)
                )
                .fetch();
    }

    @Override
    public Boolean isRecentProduct(Long userId, Long productId) {
        Long recentProductCount = queryFactory
                .select(recent.count())
                .from(recent)
                .leftJoin(recent.user, user)
                .leftJoin(recent.product, product)
                .where(
                        userIdEq(userId),
                        productIdEq(productId)
                )
                .fetchOne();
        return recentProductCount != null;
    }
}
