package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.Measurement;

import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    Optional<Measurement> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
