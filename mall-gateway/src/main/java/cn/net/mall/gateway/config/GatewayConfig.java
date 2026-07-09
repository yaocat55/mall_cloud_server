package cn.net.mall.gateway.config;

import feign.codec.Decoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关核心配置类
 *
 * <p><b>辅助配置</b> —— 当前未使用，预留备用。</p>
 *
 * <p>Gateway 基于 Reactive WebFlux，所有路由规则、CORS、白名单均已配置在 Nacos
 * （mall-gateway-dev.yaml）中，无需 Java 配置类。</p>
 *
 * <p>以下 Bean 均为历史遗留代码，当前未在 Gateway 中被引用：
 * <ul>
 *   <li>{@link RestTemplate} —— 同步 HTTP 客户端，不适用于 Reactive 环境</li>
 *   <li>{@link org.springframework.web.reactive.function.client.WebClient} —— 异步客户端，Gateway 未用到</li>
 *   <li>Feign Decoder / {@link TextHtmlJacksonConverter} —— 消息转换，Gateway 无需 Feign</li>
 * </ul>
 * </p>
 *
 * @deprecated 当前无实际用途，待后续需要时再启用
 */
@Configuration
public class GatewayConfig {

    /**
     * 配置带负载均衡的同步 HTTP 客户端
     *
     * 使用 @LoadBalanced 支持通过服务名调用，例如：
     * restTemplate.getForObject("http://user-service/api/user/1", User.class)
     *
     * @return RestTemplate 实例
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 配置 Feign 自定义消息转换器工厂
     *
     * 使 Feign 客户端能够处理 Content-Type 为 text/html 的响应，
     * 主要用于对接返回 HTML 格式但实际内容是 JSON 的遗留系统或第三方接口
     *
     * @return 消息转换器工厂
     */
    @Bean
    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        TextHtmlJacksonConverter converter = new TextHtmlJacksonConverter();
        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(converter);

        return new ObjectFactory<HttpMessageConverters>() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                return httpMessageConverters;
            }
        };
    }

    /**
     * 配置 Feign 解码器，确保自定义消息转换器生效
     *
     * 如果不需要自定义转换逻辑，可以不配置此 Bean，Feign 会使用默认解码器
     *
     * @return Feign 响应解码器
     */
    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
    }

    /**
     * 自定义 JSON 消息转换器，扩展支持 text/html 媒体类型
     *
     * 有些老旧系统或第三方接口虽然返回 JSON 数据，
     * 但 Content-Type 却错误地设置为 text/html，
     * 此类负责处理这种情况，将 HTML 格式的响应体按 JSON 解析
     */
    public static class TextHtmlJacksonConverter extends MappingJackson2HttpMessageConverter {

        public TextHtmlJacksonConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            // 支持 text/html 类型（主要目标）
            mediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
            // 保留对标准 JSON 类型的支持，避免影响正常接口
            mediaTypes.add(MediaType.APPLICATION_JSON);
            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            setSupportedMediaTypes(mediaTypes);
        }
    }

    /**
     * 配置带负载均衡的异步 HTTP 客户端
     *
     * WebClient 是响应式编程（Reactive）风格的客户端，
     * 性能优于 RestTemplate，适合高并发场景
     *
     * 使用示例：
     * webClientBuilder.build()
     *     .get()
     *     .uri("http://order-service/api/order/123")
     *     .retrieve()
     *     .bodyToMono(Order.class)
     *
     * @return WebClient.Builder 建造者实例
     */
    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}