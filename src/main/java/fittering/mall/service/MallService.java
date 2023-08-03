package fittering.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.dto.MallDto;
import fittering.mall.domain.dto.ProductPreviewDto;
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
        return mallRepository.save(new Mall(mallDto));
    }

    public Mall findById(Long mallId) {
        return mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall dosen't exist"));
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

    public List<ProductPreviewDto> findProducts(String mallName) {
        return mallRepository.findProducts(mallName);
    }
}
