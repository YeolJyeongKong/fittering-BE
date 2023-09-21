package fittering.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Dress extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "dress_id")
    private Long id;

    @NonNull
    private Double full;

    private Double shoulder;

    @NonNull
    private Double chest;

    private Double waist;
    private Double armHall;
    private Double hip;
    private Double sleeve;
    private Double sleeveWidth;
    private Double bottomWidth;

    @OneToOne(mappedBy = "dress", fetch = LAZY)
    private Size size;

    @Builder
    public Dress(Double full, Double shoulder, Double chest, Double waist, Double armHall,
                 Double hip, Double sleeve, Double sleeveWidth, Double bottomWidth) {
        this.full = full;
        this.shoulder = shoulder;
        this.chest = chest;
        this.waist = waist;
        this.armHall = armHall;
        this.hip = hip;
        this.sleeve = sleeve;
        this.sleeveWidth = sleeveWidth;
        this.bottomWidth = bottomWidth;
    }
}
