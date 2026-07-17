package cn.net.mall.inventory.client.fallback;

import cn.net.mall.feign.FeignFallbackProxy;
import cn.net.mall.inventory.client.InventoryFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * InventoryFeignClient 降级工厂.
 */
@Slf4j
public class InventoryFeignFallbackFactory implements FallbackFactory<InventoryFeignClient> {

    @Override
    public InventoryFeignClient create(Throwable cause) {
        log.error("InventoryFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(InventoryFeignClient.class, cause);
    }
}
