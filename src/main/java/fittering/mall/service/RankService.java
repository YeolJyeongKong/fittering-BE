package fittering.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.dto.MallPreviewDto;
import fittering.mall.domain.dto.MallDto;
import fittering.mall.domain.dto.MallRankProductDto;
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

    private static final int MAX_PRODUCT_COUNT = 5;
    private final UserRepository userRepository;
    private final MallRepository mallRepository;
    private final RankRepository rankRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Rank save(Long userId, Long mallId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        return rankRepository.save(new Rank(user, mall));
    }

    public Rank findById(Long rankId) {
        return rankRepository.findById(rankId)
                .orElseThrow(() -> new NoResultException("rank doesn't exist"));
    }

    public List<MallDto> mallRank(Long userId) {
        List<Mall> malls = rankRepository.mallRank(userId);
        List<MallDto> result = new ArrayList<>();

        malls.forEach(mall -> {
            List<Product> products = mall.getProducts();
            List<MallRankProductDto> productDtos = new ArrayList<>();

            int productCount = 0;
            for (Product productProxy : products) {
                if (productCount++ == MAX_PRODUCT_COUNT) break;
                Product product = productRepository.findById(productProxy.getId())
                        .orElseThrow(() -> new NoResultException("product doesn't exist"));
                productDtos.add(new MallRankProductDto(product.getId(), product.getImage()));
            }

            result.add(new MallDto(mall.getId(), mall.getName(), mall.getUrl(), mall.getImage(),
                    mall.getDescription(), mall.getRank().getView(), productDtos));
        });

        return result;
    }

    public List<MallPreviewDto> mallRankPreview(Long userId, Pageable pageable, int count) {
        return rankRepository.mallRankPreview(userId, pageable, count);
    }

    @Transactional
    public void updateViewOnProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product doesn't exist"));
        Optional<Rank> optionalRank = rankRepository.findByUserIdAndMallId(userId, product.getMall().getId());
        Rank rank = null;

        if (optionalRank.isPresent()) {
            rank = optionalRank.get();
            rank.updateView();
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Mall mall = mallRepository.findById(product.getMall().getId())
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        rank = rankRepository.save(new Rank(user, mall));

        rank.updateView();
    }

    @Transactional
    public void updateViewOnMall(Long userId, Long mallId) {
        Optional<Rank> optionalRank = rankRepository.findByUserIdAndMallId(userId, mallId);
        Rank rank = null;

        if(optionalRank.isPresent()) {
            rank = optionalRank.get();
            rank.updateView();
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));

        rank = rankRepository.save(new Rank(user, mall));

        rank.updateView();
    }
}
