package fittering.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import fittering.mall.domain.dto.DressDto;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Dress {

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

    public Dress(DressDto dressDto) {
        this.full = dressDto.getFull();
        this.shoulder = dressDto.getShoulder();
        this.chest = dressDto.getChest();
        this.waist = dressDto.getWaist();
        this.armHall = dressDto.getArmHall();
        this.hip = dressDto.getHip();
        this.sleeve = dressDto.getSleeve();
        this.sleeveWidth = dressDto.getSleeveWidth();
        this.bottomWidth = dressDto.getBottomWidth();
    }
}
