package cn.net.mall.pay.controller.notify;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.pay.enums.PayStatusEnum;
import cn.net.mall.pay.service.PayChannelService;
import cn.net.mall.pay.service.PayCoreService;
import cn.net.mall.pay.service.PayNotifyLogService;
import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.support.IdGenerator;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MOCK 模拟渠道回调 —— 仅 dev/test 环境启用.
 *
 * <p>真实支付宝/微信由渠道异步通知，MOCK 没有对端推送，需前端在模拟支付后
 * 主动请求此端点传入 merchantOrderNo，触发完整的回调处理链路。</p>
 *
 * <p>启用条件：{@code mall.pay.mock.enabled=true}。</p>
 */
@Slf4j
@RestController
@RequestMapping("/v1/notify")
@RequiredArgsConstructor
@Hidden
@ConditionalOnProperty(name = "mall.pay.mock.enabled", havingValue = "true")
public class MockNotifyController {

    private final PayNotifyLogService payNotifyLogService;
    private final PayChannelService payChannelService;
    private final PayCoreService payCoreService;
    private final IdGenerator idGenerator;

    /**
     * MOCK 模拟支付回调 —— 前端模拟支付后用 merchantOrderNo 请求此端点.
     */
    @PostMapping("/mock")
    public Map<String, Object> handleMockNotify(HttpServletRequest request,
                                                 @RequestParam("merchantOrderNo") String merchantOrderNo) {
        try {
            // 1. 读取原始报文（MOCK 无真实报文，仅记录）
            String rawBody = new BufferedReader(request.getReader()).lines()
                    .collect(Collectors.joining("\n"));

            // 2. 先落 notify_log
            PayNotifyLogEntity logEntity = new PayNotifyLogEntity();
            logEntity.setId(idGenerator.nextId());
            logEntity.setChannelCode("MOCK");
            logEntity.setNotifyType("PAY");
            logEntity.setRawData(rawBody);
            logEntity.setVerifyStatus(1); // MOCK 不验签
            logEntity.setProcessStatus(0);
            logEntity.setCreateTime(new Date());
            payNotifyLogService.insert(logEntity);

            // 3. 通过 MOCK 策略 parseNotify 构造 PayNotifyMessage
            PayChannelConfigEntity config = payChannelService.getConfig("MOCK");
            PayChannelStrategy strategy = payChannelService.getStrategy("MOCK");

            PayNotifyMessage message = strategy.parseNotify(
                    Collections.singletonMap("merchantOrderNo", merchantOrderNo),
                    rawBody, config);
            if (message == null) {
                logEntity.setProcessStatus(2);
                logEntity.setProcessMsg("MOCK 回调解析失败");
                payNotifyLogService.update(logEntity);
                return Map.of("code", "FAIL", "message", "parse failed");
            }
            // parseNotify 返回的 merchantOrderNo 可能为空，用入参兜底
            if (message.getMerchantOrderNo() == null) {
                message.setMerchantOrderNo(merchantOrderNo);
            }

            logEntity.setChannelTradeNo(message.getChannelTradeNo());

            // 4. 支付成功 → 调用 handleNotify 扭转状态
            if (PayStatusEnum.PAYMENT.getValue().equals(message.getPayStatus())) {
                payCoreService.handleNotify(
                        message.getMerchantOrderNo(),
                        message.getChannelTradeNo(),
                        message.getPayAmount(),
                        message.getSuccessTime());

                logEntity.setPayOrderNo(message.getPayOrderNo() != null
                        ? message.getPayOrderNo() : message.getMerchantOrderNo());
                logEntity.setProcessStatus(1);
                logEntity.setProcessMsg("MOCK 回调处理成功");
            } else {
                logEntity.setProcessStatus(3);
                logEntity.setProcessMsg("非支付成功通知，已记录");
            }
            payNotifyLogService.update(logEntity);

            log.info("[MOCK] 模拟回调处理完成: merchantOrderNo={}", merchantOrderNo);
            return Map.of("code", "SUCCESS", "merchantOrderNo", merchantOrderNo);

        } catch (Exception e) {
            log.error("[MOCK] 模拟回调处理异常: merchantOrderNo={}", merchantOrderNo, e);
            return Map.of("code", "FAIL", "message", "internal error");
        }
    }
}
