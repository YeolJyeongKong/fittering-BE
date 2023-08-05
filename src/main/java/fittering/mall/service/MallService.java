package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.ResponseMallDto;
import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.mapper.MallMapper;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.dto.service.MallDto;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Product;
import fittering.mall.repository.MallRepository;
import fittering.mall.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MallService {

    private final MallRepository mallRepository;
    private final ProductRepository productRepository;

    public Mall save(MallDto mallDto) {
        return mallRepository.save(MallMapper.INSTANCE.toMall(mallDto));
    }

    public ResponseMallDto findById(Long mallId) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall dosen't exist"));
        return MallMapper.INSTANCE.toResponseMallDto(mall, 0);
    }

    public Mall findByName(String mallName) {
        return mallRepository.findByName(mallName)
                .orElseThrow(() -> new NoResultException("mall dosen't exist"));
    }

    @Transactional
    public void addProduct(String mallName, Long productId) {
        Mall mall = findByName(mallName);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product dosen't exist"));
        mall.getProducts().add(product);
    }

    public List<ResponseProductPreviewDto> findProducts(String mallName) {
        return mallRepository.findProducts(mallName);
    }
}
