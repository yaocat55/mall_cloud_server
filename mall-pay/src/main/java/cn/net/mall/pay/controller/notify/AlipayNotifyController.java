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
            // 2. 提取参数为 Map
            Map<String, String> params = request.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        String[] vals = e.getValue();
                        return vals != null && vals.length > 0 ? vals[0] : "";
                    }));

            // 3. 先落 notify_log（原始报文不丢）
            PayNotifyLogEntity log = new PayNotifyLogEntity();
            log.setId(idGenerator.nextId());
            log.setChannelCode("ALIPAY");
            log.setNotifyType("PAY");
            log.setRawData(rawBody);
            log.setVerifyStatus(0);
            log.setProcessStatus(0);
            log.setCreateTime(new Date());
            payNotifyLogService.insert(log);

            // 4. 验签
            PayChannelConfigEntity config = payChannelService.getConfig("ALIPAY");
            PayChannelStrategy strategy = payChannelService.getStrategy("ALIPAY");
            if (!strategy.verifyNotify(params, rawBody, config)) {
                log.setVerifyStatus(2);
                log.setProcessStatus(2);
                log.setProcessMsg("验签失败");
                payNotifyLogService.update(log);
                return "failure";
            }
            log.setVerifyStatus(1);

            // 5. 解析回调报文 → 统一对象 → 更新支付状态
            PayNotifyMessage message = strategy.parseNotify(rawBody, config);
            if (message != null && message.getPayStatus() != null
                    && PayStatusEnum.PAYMENT.getValue().equals(message.getPayStatus())) {
                // 回调成功处理由 PayCoreService.handleNotify 完成（乐观锁流转 10 → 20）
                payCoreService.handleNotify(null, null, null, null);

                log.setChannelTradeNo(message != null ? null : null);
                log.setPayOrderId(null);
                log.setPayOrderNo(null);
                log.setProcessStatus(1);
                log.setProcessMsg("处理成功");
            } else {
                log.setProcessStatus(2);
                log.setProcessMsg("未识别的回调状态");
            }
            payNotifyLogService.update(log);
            return "success";

        } catch (Exception e) {
            log.error("支付宝回调处理异常", e);
            return "failure";
        }
    }
}
