package cn.net.mall.config;

import cn.net.mall.decoder.FeignErrorDecoder;
import cn.net.mall.decoder.FeignResultDecoder;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 全局配置.
 *
 * <p>提供自定义的 Feign Decoder 和 ErrorDecoder，通过 common-web 对所有服务生效。</p>
 *
 * <ul>
 *   <li><b>Decoder：</b>FeignResultDecoder 包装，记录调用日志（原 ApiResult 解包逻辑已注释）</li>
 *   <li><b>ErrorDecoder：</b>FeignErrorDecoder，4xx→BusinessException，5xx→RetryableException 触发重试</li>
 * </ul>
 */
@Configuration
@ConditionalOnClass(name = "feign.Feign")
public class FeignDecoderConfig {

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignDecoderConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 自定义 Feign Decoder，包装日志记录.
     */
    @Bean
    public Decoder feignDecoder() {
        return new FeignResultDecoder(
                new OptionalDecoder(
                        new ResponseEntityDecoder(
                                new SpringDecoder(this.messageConverters))));
    }

    /**
     * 全局 Feign ErrorDecoder.
     *
     * <p>所有 Feign 调用异常均经过此解码器处理：</p>
     * <ul>
     *   <li>4xx → {@link cn.net.mall.exception.BusinessException}（不触发重试）</li>
     *   <li>5xx → {@link feign.RetryableException}（触发 resilience4j 重试）</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
