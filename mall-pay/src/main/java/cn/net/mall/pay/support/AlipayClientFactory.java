package cn.net.mall.pay.support;

import cn.net.mall.pay.entity.PayChannelConfigEntity;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * AlipayClient 工厂 —— 按渠道配置创建并缓存 {@link AlipayClient} 实例.
 *
 * <p>缓存 key = channelCode，value = AlipayClient。
 * 与渠道配置缓存解耦：配置变更通过 {@link #evict(String)} 淘汰，
 * 下次请求时自然重建。</p>
 *
 * <p>设计理由：AlipayClient 的构造需要解析 config_json 取网关地址，
 * 每次请求都 new 是浪费。渠道配置几乎不变，缓存到 Caffeine 一次创建多次复用。
 * 支付流程本身不做缓存判断（配置缓存和业务状态无关）。</p>
 */
@Slf4j
@Component
public class AlipayClientFactory {

    private static final String DEFAULT_SANDBOX_GATEWAY = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** AlipayClient 缓存：max 16 个渠道，写后 30 分钟过期 */
    private final Cache<String, AlipayClient> clientCache = Caffeine.newBuilder()
            .maximumSize(16)
            .expireAfterWrite(Duration.ofMinutes(30))
            .removalListener((String key, AlipayClient client, RemovalCause cause) -> {
                if (cause.wasEvicted()) {
                    log.info("AlipayClient 缓存淘汰: channelCode={}, cause={}", key, cause);
                }
            })
            .build();

    /**
     * 获取或创建 AlipayClient（按 channelCode 缓存）.
     */
    public AlipayClient getClient(PayChannelConfigEntity config) {
        String channelCode = config.getChannelCode();
        return clientCache.get(channelCode, code -> buildClient(config));
    }

    /**
     * 淘汰指定渠道的 AlipayClient 缓存（配置变更时调用）.
     */
    public void evict(String channelCode) {
        clientCache.invalidate(channelCode);
        log.info("AlipayClient 缓存已淘汰: channelCode={}", channelCode);
    }

    /**
     * 全量淘汰（少见，仅极端情况使用）.
     */
    public void evictAll() {
        clientCache.invalidateAll();
        log.info("AlipayClient 缓存全量淘汰");
    }

    private AlipayClient buildClient(PayChannelConfigEntity config) {
        String gatewayUrl = resolveGateway(config);
        log.info("创建 AlipayClient: channelCode={}, gatewayUrl={}", config.getChannelCode(), gatewayUrl);
        return new DefaultAlipayClient(
                gatewayUrl,
                config.getAppId(),
                config.getPrivateKey(),
                AlipayConstants.FORMAT_JSON,
                CHARSET,
                config.getPublicKey(),
                SIGN_TYPE
        );
    }

    private String resolveGateway(PayChannelConfigEntity config) {
        if (config.getConfigJson() == null || config.getConfigJson().isEmpty()) {
            return DEFAULT_SANDBOX_GATEWAY;
        }
        try {
            JsonNode node = objectMapper.readTree(config.getConfigJson());
            if (node.has("gatewayUrl") && !node.get("gatewayUrl").asText().isEmpty()) {
                return node.get("gatewayUrl").asText();
            }
        } catch (Exception e) {
            log.warn("解析渠道 config_json 失败，使用默认沙箱网关: channelCode={}", config.getChannelCode(), e);
        }
        return DEFAULT_SANDBOX_GATEWAY;
    }
}
