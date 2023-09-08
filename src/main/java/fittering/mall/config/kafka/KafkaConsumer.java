package fittering.mall.config.kafka;

import fittering.mall.config.kafka.domain.KafkaProductResponse;
import fittering.mall.config.kafka.domain.dto.CrawledMallDto;
import fittering.mall.config.kafka.domain.dto.CrawledProductDto;
import fittering.mall.config.kafka.domain.dto.CrawledSizeDto;
import fittering.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ProductService productService;

    @KafkaListener(topics = "crawling-topic-0", groupId = "crawl-group-id")
    public void consumeCrawledProductsV0(String productJson) {
        updateCrawledProductsProduct(productJson);
    }

    @KafkaListener(topics = "crawling-topic-1", groupId = "crawl-group-id")
    public void consumeCrawledProductsV1(String productJson) {
        updateCrawledProductsProduct(productJson);
    }

    @KafkaListener(topics = "crawling-topic-2", groupId = "crawl-group-id")
    public void consumeCrawledProductsV2(String productJson) {
        updateCrawledProductsProduct(productJson);
    }

    @KafkaListener(topics = "crawling-topic-3", groupId = "crawl-group-id")
    public void consumeCrawledProductsV3(String productJson) {
        updateCrawledProductsProduct(productJson);
    }

    @KafkaListener(topics = "crawling-topic-4", groupId = "crawl-group-id")
    public void consumeCrawledProductsV4(String productJson) {
        updateCrawledProductsProduct(productJson);
    }

    private void updateCrawledProductsProduct(String productJson) {
        KafkaProductResponse productResponse = getProductFromJson(productJson);
        CrawledProductDto product = productResponse.getProduct();
        CrawledMallDto mall = productResponse.getMall();
        List<CrawledSizeDto> sizes = productResponse.getSize();
        List<String> imagePaths = productResponse.getImagepath();
        productService.updateCrawledProducts(product, mall, sizes, imagePaths);
    }

    private KafkaProductResponse getProductFromJson(String productJson) {
        JSONObject jsonObject = new JSONObject(productJson);
        CrawledProductDto productDto = createProductDto(jsonObject);
        CrawledMallDto mallDto = createMallDto(jsonObject);
        List<CrawledSizeDto> sizeDtos = createSizeDtos(jsonObject);
        List<String> imagePaths = createImagePaths(jsonObject);
        return KafkaProductResponse.builder()
                .product(productDto)
                .mall(mallDto)
                .size(sizeDtos)
                .imagepath(imagePaths)
                .build();
    }

    private CrawledProductDto createProductDto(JSONObject element) {
        JSONObject product = element.getJSONObject("product");
        long product_id = product.getInt("product_id");
        int price = product.getInt("price");
        String name = product.getString("name");
        String gender = product.getString("gender");
        long category_id = product.getInt("category_id");
        long sub_category_id = product.getInt("sub_category_id");
        int type = (int) (category_id - 1);
        String url = product.getString("url");
        long mall_id = product.getInt("mall_id");
        String description_path = product.getString("description_path");
        String updated_at = product.getString("updated_at");
        int disabled = product.getInt("disabled");
        return CrawledProductDto.builder()
                .product_id(product_id)
                .price(price)
                .type(type)
                .name(name)
                .gender(gender)
                .category_id(category_id)
                .sub_category_id(sub_category_id)
                .url(url)
                .mall_id(mall_id)
                .description_path(description_path)
                .updated_at(updated_at)
                .disabled(disabled)
                .build();
    }

    private CrawledMallDto createMallDto(JSONObject element) {
        JSONObject mall = element.getJSONObject("mall");
        long mall_id = mall.getInt("mall_id");
        String name = mall.getString("name");
        String url = mall.getString("url");
        String description = mall.getString("description");
        String image = mall.getString("image");
        return CrawledMallDto.builder()
                .mall_id(mall_id)
                .name(name)
                .url(url)
                .description(description)
                .image(image)
                .build();
    }

    private List<CrawledSizeDto> createSizeDtos(JSONObject element) {
        List<CrawledSizeDto> sizeDtos = new ArrayList<>();
        JSONArray sizes = element.getJSONArray("size");
        for (int i=0; i<sizes.length(); ++i) {
            JSONObject size = sizes.getJSONObject(i);
            double full = size.getDouble("full");
            double shoulder = size.getDouble("shoulder");
            double chest = size.getDouble("chest");
            double sleeve = size.getDouble("sleeve");
            double waist = size.getDouble("waist");
            double thigh = size.getDouble("thigh");
            double rise = size.getDouble("rise");
            double bottomWidth = size.getDouble("bottom_width");
            double hipWidth = size.getDouble("hip_width");
            double armHall = size.getDouble("arm_hall");
            double hip = size.getDouble("hip");
            double sleeveWidth = size.getDouble("sleeve_width");

            String name = size.getString("name");
            sizeDtos.add(CrawledSizeDto.builder()
                    .full(full == 0.0 ? null : full)
                    .shoulder(shoulder == 0.0 ? null : shoulder)
                    .chest(chest == 0.0 ? null : chest)
                    .sleeve(sleeve == 0.0 ? null : sleeve)
                    .waist(waist == 0.0 ? null : waist)
                    .thigh(thigh == 0.0 ? null : thigh)
                    .rise(rise == 0.0 ? null : rise)
                    .bottom_width(bottomWidth == 0.0 ? null : bottomWidth)
                    .hip_width(hipWidth == 0.0 ? null : hipWidth)
                    .arm_hall(armHall == 0.0 ? null : armHall)
                    .hip(hip == 0.0 ? null : hip)
                    .sleeve_width(sleeveWidth == 0.0 ? null : sleeveWidth)
                    .name(name)
                    .build());
        }
        return sizeDtos;
    }

    private List<String> createImagePaths(JSONObject element) {
        List<String> imagePaths = new ArrayList<>();
        JSONArray imagepaths = element.getJSONArray("imagepath");
        for (int i=0; i<imagepaths.length(); ++i) {
            String path = imagepaths.getString(i);
            imagePaths.add(path);
        }
        return imagePaths;
    }
}
