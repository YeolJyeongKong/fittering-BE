package fittering.mall.repository.querydsl;

import fittering.mall.domain.entity.Rank;
import org.springframework.data.domain.Pageable;
import fittering.mall.domain.dto.service.MallPreviewDto;

import java.util.List;

public interface RankRepositoryCustom {
    List<MallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count);
    List<Rank> mallRank(Long userId);
}
