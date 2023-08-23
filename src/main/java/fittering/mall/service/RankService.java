package fittering.mall.service;

import fittering.mall.domain.dto.controller.response.ResponseMallWithProductDto;
import fittering.mall.domain.dto.controller.response.ResponseMallPreviewDto;
import fittering.mall.domain.dto.controller.response.ResponseMallRankProductDto;
import fittering.mall.domain.mapper.MallMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.dto.service.MallPreviewDto;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.MallRepository;
import fittering.mall.repository.ProductRepository;
import fittering.mall.repository.RankRepository;
import fittering.mall.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankService {

    private static final int INITIAL_VIEW = 0;
    private static final int FIRST_VIEW = 1;
    private static final int MAX_PRODUCT_COUNT = 5;
    private final UserRepository userRepository;
    private final MallRepository mallRepository;
    private final RankRepository rankRepository;
    private final ProductRepository productRepository;
    private final RedisService redisService;

    @Transactional
    public Rank save(Long userId, Long mallId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        return rankRepository.save(Rank.builder()
                                    .user(user)
                                    .mall(mall)
                                    .view(INITIAL_VIEW)
                                    .build());
    }

    public Rank findById(Long rankId) {
        return rankRepository.findById(rankId)
                .orElseThrow(() -> new NoResultException("rank doesn't exist"));
    }

    public List<ResponseMallWithProductDto> mallRank(Long userId) {
        List<Rank> ranks = rankRepository.mallRank(userId);
        List<ResponseMallWithProductDto> result = new ArrayList<>();

        ranks.forEach(rank -> {
            Mall mall = rank.getMall();
            List<Product> products = mall.getProducts();
            List<ResponseMallRankProductDto> productDtos = new ArrayList<>();

            int productCount = 0;
            for (Product productProxy : products) {
                if (isEnoughProducts(productCount)) break;
                productCount++;
                Product product = productRepository.findById(productProxy.getId())
                        .orElseThrow(() -> new NoResultException("product doesn't exist"));
                productDtos.add(ResponseMallRankProductDto.builder()
                                                    .productId(product.getId())
                                                    .productImage(product.getImage())
                                                    .build());
            }

            result.add(MallMapper.INSTANCE.toResponseMallWithProductDto(mall, rank.getView()));
        });
        return result;
    }

    public List<ResponseMallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count) {
        List<MallPreviewDto> mallPreviewDtos = rankRepository.mallRankPreview(userId, pageable, count);
        List<ResponseMallPreviewDto> result = new ArrayList<>();
        mallPreviewDtos.forEach(mallPreviewDto -> {
            result.add(MallMapper.INSTANCE.toResponseMallPreviewDto(mallPreviewDto));
        });
        return result;
    }

    @Transactional
    public void updateViewOnMall(Long userId, Long mallId) {
        Optional<Rank> optionalRank = rankRepository.findByUserIdAndMallId(userId, mallId);

        if(optionalRank.isPresent()) {
            Long rankId = optionalRank.get().getId();
            redisService.batchUpdateViewOfRank(rankId);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        rankRepository.save(Rank.builder()
                                .user(user)
                                .mall(mall)
                                .view(FIRST_VIEW)
                                .build());
    }

    private boolean isEnoughProducts(int count) {
        return count >= MAX_PRODUCT_COUNT;
    }
}
