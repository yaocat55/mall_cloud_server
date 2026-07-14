package cn.net.mall.marketing.client.fallback;

import cn.net.mall.feign.FeignFallbackProxy;
import cn.net.mall.marketing.client.MarketingFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * MarketingFeignClient 降级工厂.
 */
@Slf4j
public class MarketingFeignFallbackFactory implements FallbackFactory<MarketingFeignClient> {

    @Override
    public MarketingFeignClient create(Throwable cause) {
        log.error("MarketingFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(MarketingFeignClient.class, cause);
    }
}
