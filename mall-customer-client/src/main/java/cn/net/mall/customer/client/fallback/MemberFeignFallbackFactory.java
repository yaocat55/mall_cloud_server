package cn.net.mall.customer.client.fallback;

import cn.net.mall.customer.client.MemberFeignClient;
import cn.net.mall.feign.FeignFallbackProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * MemberFeignClient 降级工厂.
 */
@Slf4j
public class MemberFeignFallbackFactory implements FallbackFactory<MemberFeignClient> {

    @Override
    public MemberFeignClient create(Throwable cause) {
        log.error("MemberFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(MemberFeignClient.class, cause);
    }
}
