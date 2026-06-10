package cn.net.mall.product.controller;

import cn.net.mall.enums.JobResult;
import cn.net.mall.product.job.RefreshIndexDataJob;
import cn.net.mall.product.job.SyncProductToEsJob;
import cn.net.mall.product.job.RecommendProductJob;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date 2025/5/11 17:12
 */
@AllArgsConstructor
@RestController
@RequestMapping("/init")
public class InitJobController {

    private final RefreshIndexDataJob refreshIndexDataJob;
    private final SyncProductToEsJob syncProductToEsJob;
    private final RecommendProductJob recommendProductJob;

    @GetMapping("/initIndexJob")
    public JobResult initIndexJob(String params) {
        return refreshIndexDataJob.doRun(params);
    }

    @GetMapping("/syncProductToES")
    public JobResult syncProductToES() {
        return syncProductToEsJob.syncProductToES();
    }

    @GetMapping("/initRecommendProduct")
    public JobResult initRecommendProduct(String params) {
        return recommendProductJob.doRun(params);
    }
}
