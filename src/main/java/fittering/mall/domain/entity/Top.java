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
public class Top {

    @Id @GeneratedValue
    @Column(name = "top_id")
    private Long id;

    @NonNull
    private Double full;

    private Double shoulder;

    @NonNull
    private Double chest;

    @NonNull
    private Double sleeve;

    @OneToOne(mappedBy = "top", fetch = LAZY)
    private Size size;

    @Builder
    public Top(Double full, Double shoulder, Double chest, Double sleeve) {
        this.full = full;
        this.shoulder = shoulder;
        this.chest = chest;
        this.sleeve = sleeve;
    }
}
