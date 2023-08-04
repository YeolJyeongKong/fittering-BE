package fittering.mall.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Mall {

    @Id @GeneratedValue
    @Column(name = "mall_id")
    private Long id;

    @NonNull @Length(max = 30)
    private String name;

    @NonNull
    private String url;

    private String image;

    @NonNull
    private String description;

    @OneToMany(mappedBy = "mall")
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "mall", fetch = LAZY)
    private Rank rank;

    @OneToMany(mappedBy = "mall")
    private List<Favorite> favorites = new ArrayList<>();

    @Builder
    public Mall(String name, String url, String image, String description) {
        this.name = name;
        this.url = url;
        this.image = image;
        this.description = description;
    }

    public void deleteRank() {
        rank = null;
    }
}
