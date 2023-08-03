package yeolJyeongKong.mall.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yeolJyeongKong.mall.service.UserService;

@Component
@RequiredArgsConstructor
public class RecommendationScheduler {

    private final UserService userService;
    private final int day = 1000 * 60 * 60 * 24;

    @Scheduled(fixedDelay = day)
    public void updateRecentlastInializedAt() {
        userService.resetUpdatedAtOfUserRecommendation();
        userService.resetUpdatedAtOfRecentRecommendation();
    }
}
