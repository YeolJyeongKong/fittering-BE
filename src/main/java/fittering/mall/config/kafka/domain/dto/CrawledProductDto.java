package fittering.mall.config.kafka.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CrawledProductDto {
    private Long product_id;
    private Integer price;
    private Integer type;
    private String name;
    private String gender;
    private Long category_id;
    private Long sub_category_id;
    private String url;
    private Long mall_id;
    private String description_path;
    private String updated_at;
    private Integer disabled;
}
