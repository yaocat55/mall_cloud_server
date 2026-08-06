package cn.net.mall.product.message;

import cn.net.mall.inventory.client.InventoryFeignClient;
import cn.net.mall.inventory.dto.InventoryUnfreezeDTO;
import cn.net.mall.mq.consumer.AbstractMqConsumer;
import cn.net.mall.mq.core.MqMessage;
import cn.net.mall.product.dto.ShoppingCartDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存回滚消费者 — 下单失败时释放已冻结的库存.
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STOCK_ROLLBACK_TOPIC",
        consumerGroup = "stock-rollback-consumer"
)
public class StockRollbackConsumer extends AbstractMqConsumer<List<ShoppingCartDTO>> {

    private final InventoryFeignClient inventoryFeignClient;

    public StockRollbackConsumer(InventoryFeignClient inventoryFeignClient, ObjectMapper objectMapper) {
        super(objectMapper.getTypeFactory().constructCollectionType(List.class, ShoppingCartDTO.class), objectMapper);
        this.inventoryFeignClient = inventoryFeignClient;
    }

    @Override
    protected void onPayload(List<ShoppingCartDTO> items, MqMessage<List<ShoppingCartDTO>> envelope, MessageExt raw) {
        if (items == null || items.isEmpty()) {
            log.warn("[MQ] 库存回滚消息 payload 为空 msgId={}", envelope.getMsgId());
            return;
        }
        for (ShoppingCartDTO item : items) {
            InventoryUnfreezeDTO dto = new InventoryUnfreezeDTO();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            inventoryFeignClient.unfreeze(dto);
        }
        log.info("[MQ] 库存回滚成功 msgId={} items={}", envelope.getMsgId(), items.size());
    }
}
