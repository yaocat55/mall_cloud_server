package cn.net.mall.pay.controller.notify;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.channel.wechat.WechatPayStrategy;
import cn.net.mall.pay.channel.wechat.WechatMiniStrategy;
import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.pay.enums.PayStatusEnum;
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
 * 微信渠道异步通知直连 mall-pay。</p>
 *
 * <p>WECHAT_PAY 和 WECHAT_MINI 共用此回调端点——它们的回调报文格式相同，
 * 通过验签 + AES-256-GCM 解密后，根据 out_trade_no 反查支付单确定渠道。</p>
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

            // 2. 先落 notify_log（原始报文不丢）
            PayNotifyLogEntity logEntity = new PayNotifyLogEntity();
            logEntity.setId(idGenerator.nextId());
            logEntity.setChannelCode("WECHAT_PAY");
            logEntity.setNotifyType("PAY");
            logEntity.setRawData(rawBody);
            logEntity.setVerifyStatus(0);
            logEntity.setProcessStatus(0);
            logEntity.setCreateTime(new Date());
            payNotifyLogService.insert(logEntity);

            // 3. 验签 + 解密 + 解析回调报文（SDK NotificationParser 一次性处理）
            PayChannelConfigEntity config = payChannelService.getConfig("WECHAT_PAY");
            PayChannelStrategy strategy = payChannelService.getStrategy("WECHAT_PAY");

            // 使用策略的 parseNotifyWithHeaders 方法（内部用 NotificationParser 验签+解密）
            PayNotifyMessage message = null;
            if (strategy instanceof WechatPayStrategy wechatStrategy) {
                message = wechatStrategy.parseNotifyWithHeaders(
                        wechatpaySignature, wechatpayTimestamp,
                        wechatpayNonce, wechatpaySerial, rawBody, config);
            }

            if (message == null || message.getMerchantOrderNo() == null) {
                logEntity.setVerifyStatus(2);
                logEntity.setProcessStatus(2);
                logEntity.setProcessMsg("回调验签解密失败：无法提取商户订单号");
                payNotifyLogService.update(logEntity);
                return "{\"code\":\"FAIL\",\"message\":\"signature verification failed\"}";
            }

            logEntity.setVerifyStatus(1);
            logEntity.setChannelTradeNo(message.getChannelTradeNo());

            // 4. 只有支付成功才更新支付单状态
            if (PayStatusEnum.PAYMENT.getValue().equals(message.getPayStatus())) {
                payCoreService.handleNotify(
                        message.getMerchantOrderNo(),
                        message.getChannelTradeNo(),
                        message.getPayAmount(),
                        message.getSuccessTime());

                logEntity.setPayOrderNo(message.getPayOrderNo() != null
                        ? message.getPayOrderNo() : message.getMerchantOrderNo());
                logEntity.setProcessStatus(1);
                logEntity.setProcessMsg("处理成功");
            } else {
                logEntity.setProcessStatus(3);
                logEntity.setProcessMsg("非支付成功通知，已记录");
            }
            payNotifyLogService.update(logEntity);
            return "{\"code\":\"SUCCESS\"}";

        } catch (Exception e) {
            log.error("微信回调处理异常", e);
            return "{\"code\":\"FAIL\",\"message\":\"internal error\"}";
        }
    }
}
