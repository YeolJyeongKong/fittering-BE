package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import yeolJyeongKong.mall.domain.dto.OuterDto;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Outer {

    @Id
    @GeneratedValue
    @Column(name = "outer_id")
    private Long id;

    @NonNull
    private Double full;

    private Double shoulder;

    @NonNull
    private Double chest;

    @NonNull
    private Double sleeve;

    @OneToOne(mappedBy = "outer", fetch = LAZY)
    private Size size;

    public Outer(OuterDto outerDto) {
        this.full = outerDto.getFull();
        this.shoulder = outerDto.getShoulder();
        this.chest = outerDto.getChest();
        this.sleeve = outerDto.getSleeve();
    }
}
