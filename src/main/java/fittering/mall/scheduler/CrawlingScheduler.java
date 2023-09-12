package fittering.mall.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import fittering.mall.config.kafka.KafkaProducer;
import fittering.mall.domain.dto.scheduler.ProductIdsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlingScheduler {

    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate;

    @Value("${ML.API.PRODUCT.ENCODE}")
    private String ML_VECTOR_UPDATE_API;

    private final int TWO_WEEK = 1000 * 60 * 60 * 24 * 14;

    @Scheduled(fixedDelay = TWO_WEEK)
    public void updateCrawledProducts() throws JsonProcessingException {
        List<String> allProductsJson = kafkaProducer.callCrawledProductCountAPI();

        URI uri = UriComponentsBuilder.fromUriString(ML_VECTOR_UPDATE_API)
                .build()
                .toUri();
        List<Long> productIds = productsJsonToString(allProductsJson);
        ProductIdsDto productIdsDto = ProductIdsDto.builder()
                .product_ids(productIds)
                .build();

        restTemplate.postForObject(uri, productIdsDto, String.class);
    }

    public static List<Long> productsJsonToString(List<String> allProductsJson) {
        List<Long> productIds = new ArrayList<>();
        for (String productJson : allProductsJson) {
            JSONObject jsonObject = new JSONObject(productJson);
            JSONObject product = jsonObject.getJSONObject("product");
            productIds.add((long) product.getInt("product_id"));
        }
        return productIds;
    }
}
