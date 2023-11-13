package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.service.dto.ProductDescriptionDto;
import fittering.mall.service.dto.QProductDescriptionDto;
import jakarta.persistence.EntityManager;

import java.util.List;

import static fittering.mall.domain.entity.QProductDescription.productDescription;
import static fittering.mall.repository.querydsl.EqualMethod.productIdEq;

public class ProductDescriptionRepositoryImpl implements ProductDescriptionRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ProductDescriptionRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ProductDescriptionDto> getProductDescriptions(Long productId) {
        return queryFactory
                .select(new QProductDescriptionDto(
                        productDescription.url
                ))
                .from(productDescription)
                .where(
                        productIdEq(productId)
                )
                .fetch();
    }
}
