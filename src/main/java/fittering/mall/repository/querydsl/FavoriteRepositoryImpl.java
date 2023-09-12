package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.domain.dto.controller.response.QResponseProductPreviewDto;
import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import fittering.mall.domain.entity.Favorite;

import java.util.List;

import static fittering.mall.domain.entity.QFavorite.favorite;
import static fittering.mall.domain.entity.QMall.mall;
import static fittering.mall.domain.entity.QProduct.product;
import static fittering.mall.domain.entity.QUser.user;
import static fittering.mall.repository.querydsl.EqualMethod.*;

public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private static int PRODUCT_PREVIEW_MAX_SIZE = 4;
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
    public Page<ResponseProductPreviewDto> userFavoriteProduct(Long userId, Pageable pageable) {

        List<ResponseProductPreviewDto> content = queryFactory
                .select(new QResponseProductPreviewDto(
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
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long nullableCount = queryFactory
                .select(favorite.count())
                .from(favorite)
                .where(
                        userIdEq(userId),
                        favorite.mall.isNull()
                )
                .fetchOne();

        Long count = nullableCount != null ? nullableCount : 0L;

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @Override
    public List<ResponseProductPreviewDto> userFavoriteProductPreview(Long userId) {
        return queryFactory
                .select(new QResponseProductPreviewDto(
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
                .orderBy(product.id.desc())
                .limit(PRODUCT_PREVIEW_MAX_SIZE)
                .fetch();
    }

    @Override
    public void deleteByUserIdAndMallId(Long userId, Long mallId) {
        queryFactory
                .delete(favorite)
                .where(
                        favoriteUserIdEq(userId),
                        favoriteMallIdEq(mallId)
                )
                .execute();
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        queryFactory
                .delete(favorite)
                .where(
                        favoriteUserIdEq(userId),
                        favoriteProductIdEq(productId)
                )
                .execute();
    }

    @Override
    public Boolean isUserFavoriteMall(Long userId, Long mallId) {
        Long count = queryFactory
                .select(favorite.count())
                .from(favorite)
                .where(
                        favoriteUserIdEq(userId),
                        favoriteMallIdEq(mallId)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Boolean isUserFavoriteProduct(Long userId, Long productId) {
        Long count = queryFactory
                .select(favorite.count())
                .from(favorite)
                .where(
                        favoriteUserIdEq(userId),
                        favoriteProductIdEq(productId)
                )
                .fetchOne();

        return count != null && count > 0;
    }
}
