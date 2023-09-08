package fittering.mall.config.kafka.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CrawledSizeDto {
    private Double full;
    private Double shoulder;
    private Double chest;
    private Double sleeve;
    private Double waist;
    private Double thigh;
    private Double rise;
    private Double bottom_width;
    private Double hip_width;
    private Double arm_hall;
    private Double hip;
    private Double sleeve_width;
    private String name;
}
