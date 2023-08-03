package fittering.mall.repository.querydsl;

import org.springframework.data.domain.Pageable;
import fittering.mall.domain.dto.MallPreviewDto;
import fittering.mall.domain.entity.Mall;

import java.util.List;

public interface RankRepositoryCustom {
    List<MallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count);
    List<Mall> mallRank(Long userId);
}
