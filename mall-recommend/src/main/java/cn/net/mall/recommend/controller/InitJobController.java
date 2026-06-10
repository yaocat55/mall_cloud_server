package cn.net.mall.recommend.controller;

import cn.net.mall.enums.JobResult;
import cn.net.mall.recommend.job.RecommendProductJob;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/init")
public class InitJobController {

    private final RecommendProductJob recommendProductJob;

    @GetMapping("/initRecommendProduct")
    public JobResult initRecommendProduct(String params) {
        return recommendProductJob.doRun(params);
    }
}
