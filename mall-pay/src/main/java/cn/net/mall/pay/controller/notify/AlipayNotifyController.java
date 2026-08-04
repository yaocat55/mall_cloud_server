package cn.net.mall.pay.controller.notify;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayNotifyLogEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付宝支付回调.
 *
 * <p>路径 {@code /v1/notify/alipay} 在网关白名单放行（免 token 鉴权），
 * 支付宝渠道异步通知直连 mall-pay。</p>
 */
@Slf4j
@RestController
@RequestMapping("/v1/notify")
@RequiredArgsConstructor
@Hidden
public class AlipayNotifyController {

    private final PayNotifyLogService payNotifyLogService;
    private final PayChannelService payChannelService;
    private final PayCoreService payCoreService;
    private final IdGenerator idGenerator;

    @PostMapping("/alipay")
    public String handleAlipayNotify(HttpServletRequest request) {
        try {
            // 1. 读取原始报文
            String rawBody = new BufferedReader(request.getReader()).lines()
                    .collect(Collectors.joining("\n"));

            // 2. 提取回调参数为 Map
            Map<String, String> params = request.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        String[] vals = e.getValue();
                        return vals != null && vals.length > 0 ? vals[0] : "";
                    }));

            // 3. 先落 notify_log（原始报文不丢）
            PayNotifyLogEntity logEntity = new PayNotifyLogEntity();
            logEntity.setId(idGenerator.nextId());
            logEntity.setChannelCode("ALIPAY");
            logEntity.setNotifyType("PAY");
            logEntity.setRawData(rawBody);
            logEntity.setVerifyStatus(0);
            logEntity.setProcessStatus(0);
            logEntity.setCreateTime(new Date());
            payNotifyLogService.insert(logEntity);

            // 4. 验签
            PayChannelConfigEntity config = payChannelService.getConfig("ALIPAY");
            PayChannelStrategy strategy = payChannelService.getStrategy("ALIPAY");
            if (!strategy.verifyNotify(params, rawBody, config)) {
                logEntity.setVerifyStatus(2);
                logEntity.setProcessStatus(2);
                logEntity.setProcessMsg("验签失败");
                payNotifyLogService.update(logEntity);
                return "failure";
            }
            logEntity.setVerifyStatus(1);

            // 5. 解析回调报文 → 统一对象
            PayNotifyMessage message = strategy.parseNotify(params, rawBody, config);
            if (message == null || message.getMerchantOrderNo() == null) {
                logEntity.setProcessStatus(2);
                logEntity.setProcessMsg("回调报文解析失败：无法提取商户订单号");
                payNotifyLogService.update(logEntity);
                return "failure";
            }

            // 记录渠道交易号
            logEntity.setChannelTradeNo(message.getChannelTradeNo());

            // 6. 只有支付成功才更新支付单状态
            if (PayStatusEnum.PAYMENT.getValue().equals(message.getPayStatus())) {
                payCoreService.handleNotify(
                        message.getMerchantOrderNo(),
                        message.getChannelTradeNo(),
                        message.getPayAmount(),
                        message.getSuccessTime());

                logEntity.setPayOrderNo(message.getPayOrderNo() != null ? message.getPayOrderNo() : message.getMerchantOrderNo());
                logEntity.setProcessStatus(1);
                logEntity.setProcessMsg("处理成功");
            } else {
                // 非支付成功通知（如退款通知），仅记录
                logEntity.setProcessStatus(3);
                logEntity.setProcessMsg("非支付成功通知，已记录");
            }
            payNotifyLogService.update(logEntity);
            return "success";

        } catch (Exception e) {
            log.error("支付宝回调处理异常", e);
            return "failure";
        }
    }
}
