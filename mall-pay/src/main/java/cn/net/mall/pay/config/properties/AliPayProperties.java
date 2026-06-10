package cn.net.mall.pay.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "susan.pay.alipay")
public class AliPayProperties {

    private String protocol;
    private String gatewayHost;
    private String signType;
    private String appId;
    private String privateKey;
    private String publicKey;
    private String notifyUrl;
}
