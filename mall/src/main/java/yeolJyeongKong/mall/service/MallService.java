package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.MallRankProductDto;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.repository.MallRepository;
import yeolJyeongKong.mall.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MallService {

    private final MallRepository mallRepository;
    private final ProductRepository productRepository;

    public List<MallDto> userFavoriteMall(Long userId) {

        List<Mall> malls = mallRepository.userFavoriteMall(userId);
        List<MallDto> result = new ArrayList<>();

        for (Mall mall : malls) {

            List<Product> products = mall.getProducts();
            List<MallRankProductDto> productDtos = new ArrayList<>();

            /**
             * 해당 쇼핑몰에서 노출시킬 상품 개수 설정 필요
             * 현재는 전부로 설정
             */
            for (Product productProxy : products) {
                Product product = productRepository.findById(productProxy.getId())
                        .orElseThrow(() -> new NoResultException("product dosen't exist"));
                productDtos.add(new MallRankProductDto(product.getId(), product.getImage()));
            }

            result.add(new MallDto(mall.getName(), mall.getUrl(), mall.getImage(), productDtos));
        }

        return result;
    }
}
