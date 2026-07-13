package cn.net.mall.config;

import cn.net.mall.handler.GlobalApiResultHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 统一响应包装配置，由 {@link EnableApiResultWrapper} 导入。
 *
 * <p>注册 {@link GlobalApiResultHandler}，对 {@code /v1/**} 的响应自动包装为 {@code ApiResult} 格式。
 * 仅在明确需要统一响应结构的服务中启用（如直接对外暴露的 BFF）。</p>
 */
@Configuration
public class ApiResultWrapperConfiguration {

    @Bean
    public GlobalApiResultHandler globalApiResultHandler() {
        return new GlobalApiResultHandler();
    }
}
