package fittering.mall.config.kafka.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CrawledMallDto {
    private Long mall_id;
    private String name;
    private String url;
    private String description;
    private String image;
}
