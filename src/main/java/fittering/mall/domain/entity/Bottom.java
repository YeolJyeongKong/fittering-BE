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
public class Bottom extends BaseEntity {

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

    @Builder
    public Bottom(Double full, Double waist, Double thigh, Double rise, Double bottomWidth, Double hipWidth) {
        this.full = full;
        this.waist = waist;
        this.thigh = thigh;
        this.rise = rise;
        this.bottomWidth = bottomWidth;
        this.hipWidth = hipWidth;
    }
}
