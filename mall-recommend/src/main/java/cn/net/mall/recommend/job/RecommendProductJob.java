package cn.net.mall.recommend.job;

import cn.net.mall.enums.JobResult;
import cn.net.mall.recommend.service.RecommendService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@AllArgsConstructor
@Slf4j
@Component
public class RecommendProductJob {

    private final RecommendService recommendService;

    public JobResult doRun(String params) {
        try {
            JobResult r1 = recommendService.recommendItemSimilaritiesToRedis();
            JobResult r2 = recommendService.recommendProductToRedis();
            return r1 == JobResult.FAILURE || r2 == JobResult.FAILURE ? JobResult.FAILURE : JobResult.SUCCESS;
        } catch (Exception e) {
            log.error("RecommendProductJob 执行失败：", e);
            return JobResult.FAILURE;
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void schedule() {
        doRun(null);
    }
}
