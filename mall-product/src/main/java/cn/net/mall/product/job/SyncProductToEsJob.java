package cn.net.mall.product.job;

import cn.net.mall.enums.JobResult;
import cn.net.mall.product.service.SyncProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 同步商品数据ES
 *
 * @date 2024/5/14 下午4:44
 */
@AllArgsConstructor
@Slf4j
@Component
public class SyncProductToEsJob {

    private final SyncProductService syncProductService;

    /**
     * 同步商品到ES
     */
    public JobResult syncProductToES() {
        try {
            syncProductService.syncProductToES();
            return JobResult.SUCCESS;
        } catch (Exception e) {
            log.error("同步商品到ES失败：", e);
            return JobResult.FAILURE;
        }
    }
}
