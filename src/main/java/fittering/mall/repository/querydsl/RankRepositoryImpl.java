package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.dto.*;
import fittering.mall.domain.entity.*;

import java.util.List;

import static fittering.mall.domain.entity.QMall.mall;
import static fittering.mall.domain.entity.QRank.rank;
import static fittering.mall.domain.entity.QUser.user;
import static fittering.mall.repository.querydsl.EqualMethod.userIdEq;

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
}
