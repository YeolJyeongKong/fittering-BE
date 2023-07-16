package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.dto.MallPreviewDto;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.MallRankProductDto;
import yeolJyeongKong.mall.domain.entity.*;
import yeolJyeongKong.mall.repository.MallRepository;
import yeolJyeongKong.mall.repository.ProductRepository;
import yeolJyeongKong.mall.repository.RankRepository;
import yeolJyeongKong.mall.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository userRepository;
    private final MallRepository mallRepository;
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

            result.add(new MallDto(mall.getName(), mall.getUrl(), mall.getImage(), mall.getDescription(), productDtos));
        }

        return result;
    }

    public List<MallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count) {
        return rankRepository.mallRankPreview(userId, pageable, count);
    }

    @Transactional
    public void updateView(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product doesn't exist"));
        Optional<Rank> optionalRank = rankRepository.findByUserIdAndMallId(userId, product.getMall().getId());
        Rank rank = null;

        if(optionalRank.isPresent()) {
            rank = optionalRank.get();
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoResultException("user doesn't exist"));
            Mall mall = mallRepository.findById(product.getMall().getId())
                    .orElseThrow(() -> new NoResultException("mall doesn't exist"));
            rank = new Rank(user, mall);
        }

        rank.updateView();
    }
}
