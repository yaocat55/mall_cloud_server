package cn.net.mall.admin.client.fallback;

import cn.net.mall.admin.client.UserFeignClient;
import cn.net.mall.feign.FeignFallbackProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * UserFeignClient 降级工厂.
 */
@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("UserFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(UserFeignClient.class, cause);
    }
}
