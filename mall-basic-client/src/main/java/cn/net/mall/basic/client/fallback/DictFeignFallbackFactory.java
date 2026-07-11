package cn.net.mall.basic.client.fallback;

import cn.net.mall.basic.client.DictFeignClient;
import cn.net.mall.feign.FeignFallbackProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * DictFeignClient 降级工厂.
 */
@Slf4j
@Component
public class DictFeignFallbackFactory implements FallbackFactory<DictFeignClient> {

    @Override
    public DictFeignClient create(Throwable cause) {
        log.error("DictFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(DictFeignClient.class, cause);
    }
}
