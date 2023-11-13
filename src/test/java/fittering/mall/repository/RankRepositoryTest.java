package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Rank;
import fittering.mall.domain.entity.User;
import fittering.mall.service.dto.MallPreviewDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class RankRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MallRepository mallRepository;

    @DisplayName("유저 id와 쇼핑몰 id로 랭킹을 조회할 수 있다.")
    @Test
    void findByUserIdAndMallId() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Rank rank = rankRepository.save(Rank.builder()
                .user(user)
                .mall(mall)
                .build());

        //when
        Optional<Rank> target = rankRepository.findByUserIdAndMallId(user.getId(), mall.getId());

        //then
        assertThat(target).isNotEmpty();
        assertThat(target.get())
                .extracting("id", "user.id", "mall.id")
                .containsExactlyInAnyOrder(
                        rank.getId(), user.getId(), mall.getId()
                );
    }

    @DisplayName("유저 id로 랭킹을 삭제할 수 있다.")
    @Test
    void deleteByUserId() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Rank rank = rankRepository.save(Rank.builder()
                .user(user)
                .mall(mall)
                .build());

        //when
        rankRepository.deleteByUserId(user.getId());

        //then
        Optional<Rank> target = rankRepository.findByUserIdAndMallId(user.getId(), mall.getId());
        assertThat(target).isEmpty();
    }

    @DisplayName("미리보기 버전에서 쇼핑몰 랭킹을 조회할 수 있다.")
    @Test
    void mallRankPreview() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Rank rank = rankRepository.save(Rank.builder()
                .user(user)
                .mall(mall)
                .build());

        //when
        List<MallPreviewDto> target = rankRepository.mallRankPreview(user.getId(), PageRequest.of(0, 20), 1);

        //then
        assertThat(target).hasSize(1)
                .extracting("id", "name", "image")
                .containsExactlyInAnyOrder(
                        tuple(mall.getId(), mall.getName(), mall.getImage())
                );
    }

    @DisplayName("쇼핑몰 랭킹을 조회할 수 있다.")
    @Test
    void mallRank() {
        //given
        User user = userRepository.save(createUser());
        Mall mall = mallRepository.save(createMall());
        Rank rank = rankRepository.save(Rank.builder()
                .user(user)
                .mall(mall)
                .build());

        //when
        List<Rank> target = rankRepository.mallRank(user.getId());

        //then
        assertThat(target).hasSize(1)
                .extracting("id", "user.id", "mall.id")
                .containsExactlyInAnyOrder(
                        tuple(rank.getId(), user.getId(), mall.getId())
                );
    }

    private User createUser() {
        return User.builder()
                .username("testuser")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(0)
                .roles(List.of("ROLE_USER"))
                .build();
    }

    private Mall createMall() {
        return Mall.builder()
                .name("열졍콩몰")
                .url("https://www.test-mall.com")
                .image("image.jpg")
                .description("description")
                .build();
    }
}