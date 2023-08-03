package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.QProductPreviewDto;

import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QFavorite.favorite;
import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.repository.querydsl.EqualMethod.favoriteIdEq;
import static yeolJyeongKong.mall.repository.querydsl.EqualMethod.mallNameEq;

public class MallRepositoryImpl implements MallRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public MallRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ProductPreviewDto> findProducts(String mallName) {
        return queryFactory
                .select(new QProductPreviewDto(
                        product.id.as("productId"),
                        product.image.as("productImage"),
                        product.name.as("productName"),
                        product.price,
                        mall.name.as("mallName"),
                        mall.url.as("mallUrl")
                ))
                .from(mall)
                .leftJoin(mall.products, product)
                .where(
                        mallNameEq(mallName)
                )
                .fetch();
    }

    @Override
    public Long findFavoriteCount(Long favoriteId) {
        Long nullableCount = queryFactory
                .select(mall.count())
                .from(mall)
                .leftJoin(mall.favorites, favorite)
                .where(
                        favoriteIdEq(favoriteId)
                )
                .fetchOne();
        return nullableCount != null ? nullableCount : 0L;
    }
}
