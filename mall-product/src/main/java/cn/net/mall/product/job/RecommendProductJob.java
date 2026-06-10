package cn.net.mall.product.job;

import cn.net.mall.enums.JobResult;
import cn.net.mall.product.service.RecommendProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class RecommendProductJob {

    private final RecommendProductService recommendProductService;

    public JobResult doRun(String params) {
        try {
            return recommendProductService.recommendProductToRedis();
        } catch (Exception e) {
            log.error("RecommendProductJob 执行失败：", e);
            return JobResult.FAILURE;
        }
    }
}
