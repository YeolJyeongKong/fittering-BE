package fittering.mall.config.kafka.domain;

import fittering.mall.config.kafka.domain.dto.CrawledMallDto;
import fittering.mall.config.kafka.domain.dto.CrawledProductDto;
import fittering.mall.config.kafka.domain.dto.CrawledSizeDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class KafkaProductResponse {
    private CrawledProductDto product;
    private CrawledMallDto mall;
    private List<CrawledSizeDto> size;
    private List<String> imagePath;
}
