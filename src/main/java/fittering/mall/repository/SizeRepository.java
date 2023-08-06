package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Size;

public interface SizeRepository extends JpaRepository<Size, Long> {
}
