package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import yeolJyeongKong.mall.domain.dto.TopDto;

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

    public Top(TopDto topDto) {
        this.full = topDto.getFull();
        this.shoulder = topDto.getShoulder();
        this.chest = topDto.getChest();
        this.sleeve = topDto.getSleeve();
    }
}
