package fittering.mall.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import fittering.mall.service.UserService;

@Component
@RequiredArgsConstructor
public class RecentScheduler {

    private final int day = 1000 * 60 * 60 * 24;

    private final UserService userService;

    @Scheduled(fixedDelay = day)
    public void updateRecentLastInitializedAt() {
        userService.updateRecentLastInitializedAtOfUsers();
    }
}
