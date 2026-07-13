package cn.net.mall.basic.client.fallback;

import cn.net.mall.basic.client.SmsRecordFeignClient;
import cn.net.mall.feign.FeignFallbackProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * SmsRecordFeignClient 降级工厂.
 */
@Slf4j
public class SmsRecordFeignFallbackFactory implements FallbackFactory<SmsRecordFeignClient> {

    @Override
    public SmsRecordFeignClient create(Throwable cause) {
        log.error("SmsRecordFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(SmsRecordFeignClient.class, cause);
    }
}
