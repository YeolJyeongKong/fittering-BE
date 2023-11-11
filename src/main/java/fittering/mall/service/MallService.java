package fittering.mall.service;

import fittering.mall.controller.dto.response.*;
import fittering.mall.service.dto.MallNameAndIdDto;
import fittering.mall.domain.mapper.MallMapper;
import fittering.mall.repository.FavoriteRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.service.dto.MallDto;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Product;
import fittering.mall.repository.MallRepository;
import fittering.mall.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MallService {

    private static final int MAX_PRODUCT_COUNT = 5;

    private final MallRepository mallRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Mall save(MallDto mallDto) {
        return mallRepository.save(MallMapper.INSTANCE.toMall(mallDto));
    }

    public ResponseMallDto findById(Long userId, Long mallId) {
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall dosen't exist"));
        Boolean isFavorite = favoriteRepository.isUserFavoriteMall(userId, mallId);
        return MallMapper.INSTANCE.toResponseMallDto(mall, 0, isFavorite);
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

    public List<ResponseMallNameAndIdDto> findMallNameAndIdList() {
        List<ResponseMallNameAndIdDto> responseMallNameAndIdList = new ArrayList<>();
        List<MallNameAndIdDto> mallNameAndIdList = mallRepository.findMallNameAndIdList();
        mallNameAndIdList.forEach(mallNameAndId -> {
            responseMallNameAndIdList.add(MallMapper.INSTANCE.toResponseMallNameAndIdDto(mallNameAndId));
        });
        return responseMallNameAndIdList;
    }

    public List<ResponseMallWithProductDto> findAll() {
        List<ResponseMallWithProductDto> responseMallWithProductDtoList = new ArrayList<>();
        mallRepository.findAll().forEach(mall -> {
            List<Product> products = mall.getProducts();
            List<ResponseMallRankProductDto> productDtos = new ArrayList<>();

            getProductDtos(products, productDtos);
            responseMallWithProductDtoList.add(MallMapper.INSTANCE.toResponseMallWithProductDto(mall, productDtos, 0, false));
        });
        return responseMallWithProductDtoList;
    }

    private void getProductDtos(List<Product> products, List<ResponseMallRankProductDto> productDtos) {
        int productCount = 0;
        for (Product productProxy : products) {
            if (isEnoughProducts(productCount)) break;
            productCount++;
            Product product = productRepository.findById(productProxy.getId())
                    .orElseThrow(() -> new NoResultException("product doesn't exist"));
            productDtos.add(ResponseMallRankProductDto.builder()
                    .productId(product.getId())
                    .productImage(product.getImage())
                    .productName(product.getName())
                    .build());
        }
    }

    private static boolean isEnoughProducts(int count) {
        return count >= MAX_PRODUCT_COUNT;
    }
}
