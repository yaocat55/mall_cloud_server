package cn.net.mall.order.client.fallback;

import cn.net.mall.feign.FeignFallbackProxy;
import cn.net.mall.order.client.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * OrderFeignClient 降级工厂.
 */
@Slf4j
public class OrderFeignFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        log.error("OrderFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(OrderFeignClient.class, cause);
    }
}
