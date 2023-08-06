package fittering.mall.scheduler;

import fittering.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewScheduler {

    private final ProductService productService;
    private final int day = 1000 * 60 * 60 * 24;

    @Scheduled(fixedDelay = day)
    public void initializeTimeView() {
        productService.initializeTimeView();
    }
}
