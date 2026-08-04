package cn.net.mall.pay.controller.notify;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.pay.service.PayChannelService;
import cn.net.mall.pay.service.PayCoreService;
import cn.net.mall.pay.service.PayNotifyLogService;
import cn.net.mall.pay.support.IdGenerator;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 微信支付回调（App / 小程序）.
 *
 * <p>路径 {@code /v1/notify/wechat} 在网关白名单放行（免 token 鉴权），
 * 微信渠道异步通知直连 mall-pay。验签 + AES-256-GCM 解密后更新支付状态。</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Hidden
public class WechatNotifyController {

    private final PayNotifyLogService payNotifyLogService;
    private final PayChannelService payChannelService;
    private final PayCoreService payCoreService;
    private final IdGenerator idGenerator;

    @PostMapping("/v1/notify/wechat")
    public String handleWechatNotify(HttpServletRequest request) {
        try {
            // 1. 读取原始报文 + 验签头
            String rawBody = new BufferedReader(request.getReader()).lines()
                    .collect(Collectors.joining("\n"));
            String wechatpaySignature = request.getHeader("Wechatpay-Signature");
            String wechatpayTimestamp = request.getHeader("Wechatpay-Timestamp");
            String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
            String wechatpaySerial = request.getHeader("Wechatpay-Serial");

            // 2. 先落 notify_log
            PayNotifyLogEntity log = new PayNotifyLogEntity();
            log.setId(idGenerator.nextId());
            log.setChannelCode("WECHAT_PAY");
            log.setNotifyType("PAY");
            log.setRawData(rawBody);
            log.setVerifyStatus(0);
            log.setProcessStatus(0);
            log.setCreateTime(new Date());
            payNotifyLogService.insert(log);

            // 3. 验签 + 解密（APIv3 平台证书验签 + AES-256-GCM 解密）
            PayChannelConfigEntity config = payChannelService.getConfig("WECHAT_PAY");
            PayChannelStrategy strategy = payChannelService.getStrategy("WECHAT_PAY");
            if (!strategy.verifyNotify(null, rawBody, config)) {
                log.setVerifyStatus(2);
                log.setProcessStatus(2);
                log.setProcessMsg("验签失败");
                payNotifyLogService.update(log);
                return "{\"code\":\"FAIL\",\"message\":\"signature verification failed\"}";
            }
            log.setVerifyStatus(1);

            // 4. 解析回调报文（微信需要解密）→ 更新支付状态
            PayNotifyMessage message = strategy.parseNotify(rawBody, config);
            if (message != null) {
                payCoreService.handleNotify(null, null, null, null);
                log.setChannelTradeNo(null);
                log.setPayOrderId(null);
                log.setPayOrderNo(null);
                log.setDecryptData(null);
                log.setProcessStatus(1);
                log.setProcessMsg("处理成功");
            } else {
                log.setProcessStatus(2);
                log.setProcessMsg("未识别的回调状态");
            }
            payNotifyLogService.update(log);
            // 200 OK，无应答报文
            return null;

        } catch (Exception e) {
            log.error("微信回调处理异常", e);
            return "{\"code\":\"FAIL\",\"message\":\"internal error\"}";
        }
    }
}
