package cn.net.mall.config;

import cn.net.mall.handler.GlobalApiResultHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 统一响应包装配置，由 {@link EnableApiResultWrapper} 导入，
 * 或通过 common-web 的 {@code AutoConfiguration.imports} 自动注册。
 *
 * <p>注册 {@link GlobalApiResultHandler}，对指定前缀的 API 响应自动包装为 {@code ApiResult} 格式。
 * 路径匹配规则见 {@link GlobalApiResultHandler#matchUrl(String)}。</p>
 */
@Configuration
public class ApiResultWrapperConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalApiResultHandler globalApiResultHandler() {
        return new GlobalApiResultHandler();
    }
}
