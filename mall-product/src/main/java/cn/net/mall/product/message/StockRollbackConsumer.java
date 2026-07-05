package cn.net.mall.product.message;

import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.product.service.ProductService;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存回滚消费者
 * 
* 当 mall-order 下单失败时，发送补偿消息回滚已扣减的库存。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "STOCK_ROLLBACK_TOPIC",
        consumerGroup = "stock-rollback-consumer"
)
public class StockRollbackConsumer implements RocketMQListener<String> {

    private final ProductService productService;

    @Override
    public void onMessage(String message) {
        log.info("收到库存回滚消息: {}", message);
        try {
            List<ShoppingCartDTO> items = JSON.parseArray(message, ShoppingCartDTO.class);
            if (items == null || items.isEmpty()) {
                log.warn("库存回滚消息为空");
                return;
            }
            productService.addStockBatch(items);
            log.info("库存回滚成功, 涉及 {} 个商品", items.size());
        } catch (Exception e) {
            log.error("库存回滚失败, message={}", message, e);
            throw new RuntimeException("库存回滚失败，稍后重试", e);
        }
    }
}
