package cn.net.mall.pay.config;

import cn.net.mall.pay.config.properties.AliPayProperties;
import com.alipay.easysdk.kernel.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置
 */
@Configuration
public class AliPayConfig {

    @Autowired
    private AliPayProperties aliPayProperties;

    @Bean
    public Config config() {
        Config config = new Config();
        config.protocol = aliPayProperties.getProtocol();
        config.gatewayHost = aliPayProperties.getGatewayHost();
        config.signType = aliPayProperties.getSignType();
        config.appId = aliPayProperties.getAppId();
        config.merchantPrivateKey = aliPayProperties.getPrivateKey();
        config.alipayPublicKey = aliPayProperties.getPublicKey();
        config.notifyUrl = aliPayProperties.getNotifyUrl();
        return config;
    }
}
