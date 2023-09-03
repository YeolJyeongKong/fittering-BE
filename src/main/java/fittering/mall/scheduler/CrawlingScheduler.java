package fittering.mall.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import fittering.mall.config.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlingScheduler {

    private final KafkaProducer kafkaProducer;

    private final int TWO_WEEK = 1000 * 60 * 60 * 24 * 14;

    @Scheduled(fixedDelay = TWO_WEEK)
    public void updateCrawledProducts() throws JsonProcessingException {
        kafkaProducer.callCrawledProductCountAPI();
    }
}
