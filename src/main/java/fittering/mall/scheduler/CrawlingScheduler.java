package fittering.mall.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import fittering.mall.config.kafka.KafkaProducer;
import fittering.mall.scheduler.dto.ProductIdsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static fittering.mall.config.kafka.KafkaConsumer.newProductList;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlingScheduler {

    private final int TWO_WEEK = 1000 * 60 * 60 * 24 * 14;
    private final int DAY = 1000 * 60 * 60 * 24;
    private final Lock lock = new ReentrantLock();

    @Value("${ML.API.PRODUCT.ENCODE}")
    private String ML_VECTOR_UPDATE_API;

    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = TWO_WEEK)
    public void updateCrawledProducts() throws JsonProcessingException {
        kafkaProducer.callCrawledProductCountAPI();
    }

    @Scheduled(fixedDelay = DAY)
    public void updateMLVector() {
        if (newProductList.isEmpty()) {
            return;
        }

        lock.lock();
        URI uri = UriComponentsBuilder.fromUriString(ML_VECTOR_UPDATE_API)
                .build()
                .toUri();
        ProductIdsDto productIdsDto = ProductIdsDto.builder()
                .product_ids(newProductList)
                .build();

        restTemplate.postForObject(uri, productIdsDto, String.class);
        lock.unlock();
    }
}
