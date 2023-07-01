package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.QProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.QMall;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QRecent.recent;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class RecentRepositoryImpl implements RecentRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public RecentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 8개 노출
     * 16개 될 때마다 오래된 순으로 삭제 진행 필요
     */
    @Override
    public List<ProductPreviewDto> recentProduct(Long userId) {
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
                .leftJoin(recent.products, product)
                .leftJoin(product.mall, mall)
                .where(
                        userIdEq(userId),
                        product.recent.id.eq(recent.id)
                )
                .orderBy(recent.timestamp.desc())
                .limit(8)
                .fetch();
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
