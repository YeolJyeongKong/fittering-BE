package fittering.mall.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fittering.mall.config.kafka.domain.KafkaProductRequest;
import fittering.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final int BATCH_SIZE = 100;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProductService productService;
    private final RestTemplate restTemplate;

    private static final String CRAWLED_PRODUCT_TOPIC = "crawling-topic-";
    private static final int TOPIC_NUMBER = 5;

    @Value("${ML.API.PRODUCT.COUNT}")
    private String CRAWLED_PRODUCT_COUNT_API;
    @Value("${ML.API.PRODUCT.INFO}")
    private String CRAWLED_PRODUCT_INFO_API;

    public List<String> callCrawledProductCountAPI() throws JsonProcessingException {
        URI uri = UriComponentsBuilder.fromUriString(CRAWLED_PRODUCT_COUNT_API)
                .build()
                .toUri();
        String responseBody = restTemplate.getForObject(uri, String.class);
        int productsCount = getProductsCountFromBody(responseBody);
        LocalDateTime timeCriteria = productService.productsOfMaxUpdatedAt();
        return produceCrawledProducts(productsCount, timeCriteria);
    }

    public List<String> produceCrawledProducts(int productsCount, LocalDateTime timeCriteria) throws JsonProcessingException {
        List<String> allProductsJson = new ArrayList<>();
        int batchIndex = productsCount % BATCH_SIZE == 0 ? productsCount / BATCH_SIZE : productsCount / BATCH_SIZE + 1;
        for (int i=0; i<batchIndex; ++i) {
            URI uri = UriComponentsBuilder.fromUriString(CRAWLED_PRODUCT_INFO_API)
                    .build()
                    .toUri();

            int startId = i * BATCH_SIZE + 1;
            int endId = i < batchIndex-1 ? (i+1) * BATCH_SIZE : productsCount;
            KafkaProductRequest request = KafkaProductRequest.builder()
                    .range(List.of(startId, endId))
                    .updated_at(timeCriteriaToString(timeCriteria))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonRequest = convertProductRequestToJson(request);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonRequest, headers);
            String responseBody = restTemplate.postForObject(uri, httpEntity, String.class);
            List<String> productsJson = getProductsJsonFromBody(responseBody);
            allProductsJson.addAll(productsJson);

            int sendCount = 0;
            for (String productJson : productsJson) {
                sendCount = (sendCount + 1) % TOPIC_NUMBER;
                String topic = CRAWLED_PRODUCT_TOPIC + sendCount;
                kafkaTemplate.send(topic, productJson);
            }
        }

        return allProductsJson;
    }

    private static String timeCriteriaToString(LocalDateTime timeCriteria) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timeCriteria.format(formatter);
    }

    private static int getProductsCountFromBody(String responseBody) {
        JSONObject jObject = new JSONObject(responseBody);
        return jObject.getInt("body");
    }

    private static List<String> getProductsJsonFromBody(String responseBody) {
        List<String> productsJson = new ArrayList<>();
        JSONObject jObject = new JSONObject(responseBody);
        JSONArray jArray = jObject.getJSONArray("body");

        for (int i=0; i<jArray.length(); ++i) {
            productsJson.add(jArray.getJSONObject(i).toString());
        }

        return productsJson;
    }

    private static String convertProductRequestToJson(KafkaProductRequest request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(request);
    }
}