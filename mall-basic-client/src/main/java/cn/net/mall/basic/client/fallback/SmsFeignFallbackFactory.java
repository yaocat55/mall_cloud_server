package cn.net.mall.basic.client.fallback;

import cn.net.mall.basic.client.SmsFeignClient;
import cn.net.mall.feign.FeignFallbackProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * SmsFeignClient 降级工厂.
 */
@Slf4j
public class SmsFeignFallbackFactory implements FallbackFactory<SmsFeignClient> {

    @Override
    public SmsFeignClient create(Throwable cause) {
        log.error("SmsFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(SmsFeignClient.class, cause);
    }
}
