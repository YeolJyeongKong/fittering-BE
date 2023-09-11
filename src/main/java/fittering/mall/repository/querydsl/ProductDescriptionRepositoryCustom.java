package fittering.mall.repository.querydsl;

import fittering.mall.domain.dto.service.ProductDescriptionDto;

import java.util.List;

public interface ProductDescriptionRepositoryCustom {
    List<ProductDescriptionDto> getProductDescrtiptions(Long productId);
}
