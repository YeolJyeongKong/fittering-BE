package fittering.mall.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import fittering.mall.service.dto.MeasurementDto;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Measurement extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "measurement_id")
    private Long id;

    private Integer height;
    private Integer weight;
    private Integer arm;
    private Integer leg;
    private Integer shoulder;
    private Integer waist;
    private Integer chest;
    private Integer thigh;
    private Integer hip;

    @JsonIgnore
    @OneToOne(mappedBy = "measurement", fetch = LAZY)
    private User user;

    public Measurement() {
    }

    public void update(MeasurementDto measurementDto) {
        height = measurementDto.getHeight();
        weight = measurementDto.getWeight();
        arm = measurementDto.getArm();
        leg = measurementDto.getLeg();
        shoulder = measurementDto.getShoulder();
        waist = measurementDto.getWaist();
        chest = measurementDto.getChest();
        thigh = measurementDto.getThigh();
        hip = measurementDto.getHip();
    }
}
