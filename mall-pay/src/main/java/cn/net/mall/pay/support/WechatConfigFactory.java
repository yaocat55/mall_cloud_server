package cn.net.mall.pay.support;

import cn.net.mall.pay.entity.PayChannelConfigEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 微信支付 Config 工厂 —— 按渠道配置创建并缓存 {@link RSAAutoCertificateConfig}.
 *
 * <p>与 {@link AlipayClientFactory} 的差异：微信 SDK 的核心对象不是 HTTP 客户端，
 * 而是 {@code RSAAutoCertificateConfig}——它内部封装了 APIv3 签名、OkHttp 连接池、
 * 平台证书自动下载/轮换（{@code AutoCertificateService}，每 60 分钟检查）。</p>
 *
 * <p>缓存 key = channelCode，value = RSAAutoCertificateConfig（内含 HTTP 客户端）。
 * AppService / JsapiService / RefundService 是轻量对象，每次请求通过 Builder 创建，
 * 不需要缓存。</p>
 *
 * <p>设计理由：渠道密钥（商户私钥/apiV3Key）几乎不变，RSAAutoCertificateConfig 创建
 * 是重操作（读证书+初始化后台线程），Caffeine 缓存一次创建多次复用。</p>
 */
@Slf4j
@Component
public class WechatConfigFactory {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** RSAAutoCertificateConfig 缓存 */
    private final Cache<String, RSAAutoCertificateConfig> configCache = Caffeine.newBuilder()
            .maximumSize(16)
            .expireAfterWrite(Duration.ofMinutes(30))
            .removalListener((String key, RSAAutoCertificateConfig config, RemovalCause cause) -> {
                if (cause.wasEvicted()) {
                    log.info("微信 Config 缓存淘汰: channelCode={}, cause={}", key, cause);
                }
            })
            .build();

    /**
     * 获取或创建 RSAAutoCertificateConfig（按 channelCode 缓存）.
     */
    public RSAAutoCertificateConfig getConfig(PayChannelConfigEntity channelConfig) {
        String channelCode = channelConfig.getChannelCode();
        return configCache.get(channelCode, code -> buildConfig(channelConfig));
    }

    /**
     * 淘汰指定渠道的缓存（配置变更时调用）.
     */
    public void evict(String channelCode) {
        configCache.invalidate(channelCode);
        log.info("微信 Config 缓存已淘汰: channelCode={}", channelCode);
    }

    private RSAAutoCertificateConfig buildConfig(PayChannelConfigEntity channelConfig) {
        JsonNode configJson = parseConfigJson(channelConfig.getConfigJson());
        String apiV3Key = configJson != null && configJson.has("apiV3Key")
                ? configJson.get("apiV3Key").asText() : null;
        String certSerialNo = configJson != null && configJson.has("certSerialNo")
                ? configJson.get("certSerialNo").asText() : null;

        if (apiV3Key == null || certSerialNo == null) {
            throw new IllegalStateException(
                    "微信渠道配置缺失 apiV3Key 或 certSerialNo: channelCode=" + channelConfig.getChannelCode());
        }

        log.info("创建微信 RSAAutoCertificateConfig: channelCode={}, merchantId={}, certSerialNo={}",
                channelConfig.getChannelCode(), channelConfig.getMerchantId(), certSerialNo);

        return new RSAAutoCertificateConfig.Builder()
                .merchantId(channelConfig.getMerchantId())
                .privateKey(channelConfig.getPrivateKey())
                .merchantSerialNumber(certSerialNo)
                .apiV3Key(apiV3Key)
                .build();
    }

    private JsonNode parseConfigJson(String configJson) {
        if (configJson == null || configJson.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(configJson);
        } catch (Exception e) {
            log.warn("解析渠道 config_json 失败: {}", configJson, e);
            return null;
        }
    }
}
