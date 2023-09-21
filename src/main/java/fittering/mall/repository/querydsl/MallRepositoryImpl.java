package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.controller.dto.response.QResponseProductPreviewDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.service.dto.MallNameAndIdDto;
import fittering.mall.service.dto.QMallNameAndIdDto;
import jakarta.persistence.EntityManager;

import java.util.List;

import static fittering.mall.domain.entity.QFavorite.favorite;
import static fittering.mall.domain.entity.QMall.mall;
import static fittering.mall.domain.entity.QProduct.product;
import static fittering.mall.repository.querydsl.EqualMethod.favoriteIdEq;
import static fittering.mall.repository.querydsl.EqualMethod.mallNameEq;

public class MallRepositoryImpl implements MallRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public MallRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ResponseProductPreviewDto> findProducts(String mallName) {
        return queryFactory
                .select(new QResponseProductPreviewDto(
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

    @Override
    public List<String> relatedSearch(String keyword) {
        return queryFactory
                .select(mall.name)
                .from(mall)
                .where(
                        mall.name.contains(keyword)
                )
                .fetch();
    }

    @Override
    public List<MallNameAndIdDto> findMallNameAndIdList() {
        return queryFactory
                .select(new QMallNameAndIdDto(
                        mall.id,
                        mall.name
                ))
                .from(mall)
                .fetch();
    }
}
