package cn.net.mall.product.client.fallback;

import cn.net.mall.feign.FeignFallbackProxy;
import cn.net.mall.product.client.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * ProductFeignClient 降级工厂.
 */
@Slf4j
@Component
public class ProductFeignFallbackFactory implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        log.error("ProductFeignClient 调用失败，进入降级", cause);
        return FeignFallbackProxy.create(ProductFeignClient.class, cause);
    }
}
