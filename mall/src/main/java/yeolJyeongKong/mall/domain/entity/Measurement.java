package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.NonNull;

@Entity
public class Measurement {

    @Id @GeneratedValue
    @Column(name = "measurement_id")
    private Long id;

    @NonNull
    private Integer height;

    @NonNull
    private Integer weight;

    private Integer arm;
    private Integer leg;
    private Integer shoulder;
    private Integer waist;
    private Integer chest;
    private Integer thigh;
    private Integer hip;

    @OneToOne(mappedBy = "measurement")
    private User user;
}
