package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.QProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.Favorite;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QFavorite.favorite;
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public FavoriteRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Favorite> userFavoriteMall(Long userId) {
        return queryFactory
                .selectFrom(favorite)
                .join(favorite.user, user)
                .join(favorite.mall, mall)
                .join(mall.products, product)
                .where(
                        userIdEq(userId),
                        favorite.product.isNull()
                )
                .fetch();
    }

    @Override
    public Page<ProductPreviewDto> userFavoriteProduct(Long userId, Pageable pageable) {

        List<ProductPreviewDto> content = queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(favorite)
                .join(favorite.user, user)
                .join(favorite.product, product)
                .join(product.mall, mall)
                .where(
                        userIdEq(userId),
                        favorite.mall.isNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(favorite.count())
                .from(favorite)
                .where(
                        userIdEq(userId),
                        favorite.mall.isNull()
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public void deleteByUserIdAndMallId(Long userId, Long mallId) {
        queryFactory
                .delete(favorite)
                .where(favorite.user.id.eq(userId).and(favorite.mall.id.eq(mallId)))
                .execute();
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        queryFactory
                .delete(favorite)
                .where(favorite.user.id.eq(userId).and(favorite.product.id.eq(productId)))
                .execute();
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
