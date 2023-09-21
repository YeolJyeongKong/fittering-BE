package fittering.mall.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fittering.mall.repository.dto.QSavedMallPreviewDto;
import fittering.mall.repository.dto.SavedMallPreviewDto;
import fittering.mall.service.dto.MallPreviewDto;
import fittering.mall.domain.mapper.MallMapper;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.entity.*;

import java.util.ArrayList;
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
        List<SavedMallPreviewDto> savedMallPreviewDtos = queryFactory
                .select(new QSavedMallPreviewDto(
                        mall.id,
                        mall.name,
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

        List<MallPreviewDto> mallRankPreviewDtos = new ArrayList<>();
        savedMallPreviewDtos.forEach(savedMallPreviewDto -> {
            mallRankPreviewDtos.add(MallMapper.INSTANCE.toMallPreviewDto(savedMallPreviewDto));
        });
        return mallRankPreviewDtos;
    }

    @Override
    public List<Rank> mallRank(Long userId) {
        return queryFactory
                .selectFrom(rank)
                .leftJoin(rank.mall, mall)
                .leftJoin(rank.user, user)
                .where(
                        userIdEq(userId)
                )
                .orderBy(rank.view.desc())
                .fetch();
    }
}
