package yeolJyeongKong.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.*;
import yeolJyeongKong.mall.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import static yeolJyeongKong.mall.domain.entity.QMall.mall;
import static yeolJyeongKong.mall.domain.entity.QProduct.product;
import static yeolJyeongKong.mall.domain.entity.QRank.rank;
import static yeolJyeongKong.mall.domain.entity.QUser.user;

@RequiredArgsConstructor
public class RankRepositoryImpl implements RankRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public RankRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count) {
        return queryFactory
                .select(new QMallPreviewDto(
                        mall.name,
                        mall.url,
                        mall.image
                ))
                .from(rank)
                .leftJoin(rank.mall, mall)
                .leftJoin(rank.user, user)
                .where(
                        userIdEq(userId)
                )
                .orderBy(rank.view.desc())
                .limit(count)
                .fetch();
    }

    @Override
    public List<Mall> mallRank(Long userId) {
        return queryFactory
                .select(mall)
                .from(rank)
                .leftJoin(rank.mall, mall)
                .leftJoin(rank.user, user)
                .where(
                        userIdEq(userId)
                )
                .orderBy(rank.view.desc())
                .fetch();
    }

    public BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }
}
