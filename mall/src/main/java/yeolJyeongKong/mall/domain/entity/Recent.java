package yeolJyeongKong.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Recent {

    @Id @GeneratedValue
    @Column(name = "recent_id")
    private Long id;

    @NonNull
    private LocalDateTime timestamp;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recent", fetch = LAZY)
    private List<Product> products = new ArrayList<>();
}
