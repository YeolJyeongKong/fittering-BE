package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Mall;
import fittering.mall.service.dto.MallNameAndIdDto;
import fittering.mall.service.dto.RelatedSearchDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class MallRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MallRepository mallRepository;

    @DisplayName("이름으로 쇼핑몰을 조회할 수 있다.")
    @Test
    void findByName() {
        //given
        Mall mallInfo = createMall();

        //when
        Mall target = mallRepository.save(mallInfo);

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("name", "url", "image", "description")
                .containsExactlyInAnyOrder(
                        mallInfo.getName(),
                        mallInfo.getUrl(),
                        mallInfo.getImage(),
                        mallInfo.getDescription()
                );
    }

    @DisplayName("키워드로 쇼핑몰을 조회할 수 있다.")
    @Test
    void findMallByKeyword() {
        //given
        Mall mall = mallRepository.save(createMall());

        //when
        List<RelatedSearchDto> target = mallRepository.relatedSearch("열졍콩");

        //then
        assertThat(target).hasSize(1)
                .extracting("name", "image")
                .containsExactlyInAnyOrder(
                        tuple(mall.getName(), mall.getImage())
                );
    }

    @DisplayName("쇼핑몰의 이름과 id 리스트를 조회할 수 있다.")
    @Test
    void findMallNameAndIdList() {
        //given
        Mall mall = mallRepository.save(createMall());

        //when
        List<MallNameAndIdDto> target = mallRepository.findMallNameAndIdList();

        //then
        assertThat(target).hasSize(1)
                .extracting("name")
                .containsExactlyInAnyOrder(
                        mall.getName()
                );
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