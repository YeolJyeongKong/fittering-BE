package fittering.mall.scheduler;

import fittering.mall.service.ProductService;
import fittering.mall.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewScheduler {

    private final int day = 1000 * 60 * 60 * 24;
    private final int hour = 1000 * 60 * 60;

    private final ProductService productService;
    private final RedisService redisService;

    @Scheduled(fixedDelay = day)
    public void initializeTimeView() {
        productService.initializeTimeView();
    }

    @Scheduled(fixedDelay = hour)
    public void batchUpdateView() {
        redisService.batchUpdateView();
    }
}
