package fittering.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import fittering.mall.domain.dto.BottomDto;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Bottom {

    @Id @GeneratedValue
    @Column(name = "bottom_id")
    private Long id;

    @NonNull
    private Double full;

    @NonNull
    private Double waist;

    private Double thigh;
    private Double rise;

    @NonNull
    private Double bottomWidth;

    private Double hipWidth;

    @OneToOne(mappedBy = "bottom", fetch = LAZY)
    private Size size;

    public Bottom(BottomDto bottomDto) {
        this.full = bottomDto.getFull();
        this.waist = bottomDto.getWaist();
        this.thigh = bottomDto.getThigh();
        this.rise = bottomDto.getRise();
        this.bottomWidth = bottomDto.getBottomWidth();
        this.hipWidth = bottomDto.getHipWidth();
    }
}