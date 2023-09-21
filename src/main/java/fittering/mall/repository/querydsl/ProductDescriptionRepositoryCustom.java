package fittering.mall.repository.querydsl;

import fittering.mall.service.dto.ProductDescriptionDto;

import java.util.List;

public interface ProductDescriptionRepositoryCustom {
    List<ProductDescriptionDto> getProductDescrtiptions(Long productId);
}
