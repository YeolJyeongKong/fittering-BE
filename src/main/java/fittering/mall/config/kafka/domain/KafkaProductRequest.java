package fittering.mall.config.kafka.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class KafkaProductRequest {
    public List<Integer> range;
    public String updated_at;
}
