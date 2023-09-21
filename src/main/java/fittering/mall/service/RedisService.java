package fittering.mall.service;

import fittering.mall.repository.ProductRepository;
import fittering.mall.repository.RankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final RankRepository rankRepository;

    public void updateViewOfProduct(Long productId) {
        redisTemplate.opsForValue().increment("Batch:Product_view_" + productId);
    }

    public void updateTimeViewOfProduct(Long productId) {
        redisTemplate.opsForValue().increment("Batch:Product_timeView_" + productId);
    }

    public void batchUpdateViewOfRank(Long RankId) {
        redisTemplate.opsForValue().increment("Batch:Rank_view_" + RankId);
    }

    @Transactional
    public void batchUpdateView() {
        redisTemplate.keys("Batch:Product_view_*").forEach(key -> {
            Long productId = Long.parseLong(key.split("_")[1]);
            Integer view = Integer.parseInt(redisTemplate.opsForValue().get(key).toString());

            updateViewOfProduct(productId, view);
            redisTemplate.delete(key);
        });

        redisTemplate.keys("Batch:Product_timeView_*").forEach(key -> {
            Long productId = Long.parseLong(key.split("_")[1]);
            Integer timeView = Integer.parseInt(redisTemplate.opsForValue().get(key).toString());

            updateTimeViewOfProduct(productId, timeView);
            redisTemplate.delete(key);
        });

        redisTemplate.keys("Batch:Rank_view_*").forEach(key -> {
            Long rankId = Long.parseLong(key.split("_")[1]);
            Integer view = Integer.parseInt(redisTemplate.opsForValue().get(key).toString());

            updateViewOfRank(rankId, view);
            redisTemplate.delete(key);
        });
    }

    private void updateViewOfProduct(Long productId, Integer view) {
        productRepository.findById(productId).ifPresent(product -> {
            product.updateView(view);
        });
    }

    private void updateTimeViewOfProduct(Long productId, Integer timeView) {
        productRepository.findById(productId).ifPresent(product -> {
            product.updateTimeView(timeView);
        });
    }

    private void updateViewOfRank(Long rankId, Integer view) {
        rankRepository.findById(rankId).ifPresent(rank -> {
            rank.updateView(view);
        });
    }
}
