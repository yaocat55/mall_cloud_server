package cn.net.mall.order.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "mall.order")
public class BusinessConfig {

    private String orderTimeoutCancelTopic = "ORDER_TIMEOUT_CANCEL_TOPIC";

    private int orderTimeoutDelayLevel = 15;

    private String orderTimeoutCancelTag = "CANCEL_TIMEOUT";

    /**
     * 库存回滚 Topic（下单失败时补偿用）
     */
    private String stockRollbackTopic = "STOCK_ROLLBACK_TOPIC";
}
