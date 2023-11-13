package fittering.mall.repository;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.domain.entity.Product;
import fittering.mall.domain.entity.ProductDescription;
import fittering.mall.service.dto.ProductDescriptionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProductDescriptionRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductDescriptionRepository productDescriptionRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품 설명 정보를 조회할 수 있다.")
    @Test
    void getProductDescriptions() {
        //given
        Product product = productRepository.save(createProduct());
        productDescriptionRepository.save(ProductDescription.builder()
                .url("description.jpg")
                .product(product)
                .build());
        productDescriptionRepository.save(ProductDescription.builder()
                .url("description2.jpg")
                .product(product)
                .build());

        //when
        List<ProductDescriptionDto> target = productDescriptionRepository.getProductDescriptions(product.getId());

        //then
        assertThat(target).hasSize(2)
                .extracting("url")
                .containsExactlyInAnyOrder(
                        "description.jpg",
                        "description2.jpg"
                );
    }

    private Product createProduct() {
        return Product.builder()
                .price(1000)
                .name("product")
                .gender("M")
                .type(0)
                .image("image.jpg")
                .origin("https://test-mall.com")
                .view(0)
                .timeView(0)
                .disabled(0)
                .build();
    }
}