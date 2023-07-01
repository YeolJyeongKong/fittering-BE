package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.MallPreviewDto;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.MallRankProductDto;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.QProduct;
import yeolJyeongKong.mall.repository.ProductRepository;
import yeolJyeongKong.mall.repository.RankRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static yeolJyeongKong.mall.domain.entity.QProduct.product;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
    private final ProductRepository productRepository;

    public List<MallDto> mallRank(Long userId) {
        List<Mall> malls = rankRepository.mallRank(userId);

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
                        .orElseThrow(() -> new NoResultException("product doesn't exist"));
                productDtos.add(new MallRankProductDto(product.getId(), product.getImage()));
            }

            result.add(new MallDto(mall.getName(), mall.getUrl(), mall.getImage(), productDtos));
        }

        return result;
    }

    public List<MallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count) {
        return rankRepository.mallRankPreview(userId, pageable, count);
    }
}
