package fittering.mall.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "`outer`")
@NoArgsConstructor(access = PROTECTED)
public class Outer extends BaseEntity {

    @Id @GeneratedValue
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

    @Builder
    public Outer(Double full, Double shoulder, Double chest, Double sleeve) {
        this.full = full;
        this.shoulder = shoulder;
        this.chest = chest;
        this.sleeve = sleeve;
    }
}
