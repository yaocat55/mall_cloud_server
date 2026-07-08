package cn.net.mall.config;

import cn.net.mall.sensitive.CustomMaskService;
import cn.net.mall.sensitive.ICustomMaskService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 敏感词脱敏自动配置。
 *
 * <p>默认注册 {@link CustomMaskService} 作为 {@link ICustomMaskService} 的实现，
 * 使用 Hutool 工具库进行字符串脱敏。</p>
 *
 * <p>如需自定义脱敏策略，声明一个 {@link ICustomMaskService} 的 Bean 即可覆盖默认实现。</p>
 *
 * <p>注册的 Bean：</p>
 * <ul>
 *   <li>{@link ICustomMaskService} — 敏感词脱敏接口（默认实现 {@link CustomMaskService}）</li>
 * </ul>
 */
@AutoConfiguration
public class SensitiveAutoConfiguration {

    /**
     * 注册默认的敏感数据脱敏实现。
     * <p>使用 {@link CustomMaskService} 将所有字符替换为星号，保留第一位字符可见。</p>
     *
     * @return ICustomMaskService 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ICustomMaskService customMaskService() {
        return new CustomMaskService();
    }
}
