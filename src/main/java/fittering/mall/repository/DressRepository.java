package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Dress;

public interface DressRepository extends JpaRepository<Dress, Long> {
}
