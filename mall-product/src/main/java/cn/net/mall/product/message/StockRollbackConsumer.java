package cn.net.mall.product.message;

import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryUnfreezeDTO;
import cn.net.mall.inventory.dto.InventoryReturnDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存回滚消费者 — 下单失败时释放已冻结的库存.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "STOCK_ROLLBACK_TOPIC",
        consumerGroup = "stock-rollback-consumer"
)
public class StockRollbackConsumer implements RocketMQListener<String> {

    private final InventoryFeignClient inventoryFeignClient;

    @Override
    public void onMessage(String message) {
        log.info("收到库存回滚消息: {}", message);
        try {
            List<ShoppingCartDTO> items = JSON.parseArray(message, ShoppingCartDTO.class);
            if (items == null || items.isEmpty()) {
                log.warn("库存回滚消息为空");
                return;
            }
            for (ShoppingCartDTO item : items) {
                InventoryUnfreezeDTO dto = new InventoryUnfreezeDTO();
                dto.setProductId(item.getProductId());
                dto.setQuantity(item.getQuantity());
                inventoryFeignClient.unfreeze(dto);
            }
            log.info("库存回滚成功, 涉及 {} 个商品", items.size());
        } catch (Exception e) {
            log.error("库存回滚失败, message={}", message, e);
            throw new RuntimeException("库存回滚失败，稍后重试", e);
        }
    }
}
