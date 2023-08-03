package yeolJyeongKong.mall.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.repository.querydsl.MallRepositoryCustom;

import java.util.Optional;

public interface MallRepository extends JpaRepository<Mall, Long>, MallRepositoryCustom {
    @Override
    @EntityGraph(attributePaths = {"rank"})
    Optional<Mall> findById(Long id);
    Optional<Mall> findByName(String name);
    Optional<Mall> findByRankId(Long rankId);
}
