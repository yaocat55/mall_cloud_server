package cn.net.mall.pay.channel.mock;

import cn.net.mall.pay.channel.PayChannelStrategy;
import cn.net.mall.pay.dto.BillRow;
import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.dto.PayPrepayResult;
import cn.net.mall.pay.dto.PayQueryResult;
import cn.net.mall.pay.dto.PayRefundQueryResult;
import cn.net.mall.pay.dto.PayRefundResult;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.entity.PayRefundEntity;
import cn.net.mall.pay.enums.PayStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MOCK 模拟渠道策略（仅 dev/test 环境启用）.
 *
 * <p>用于无真实商户号时打通支付全流程（下单/回调/退款/对账）：</p>
 * <ul>
 *   <li>prepay 直接返回预设成功参数（前端拉起支付后，MOCK 会"自动支付"）</li>
 *   <li>refund 直接成功</li>
 *   <li>downloadBill 返回内存构造的对账单，支持对账链路联调</li>
 * </ul>
 * <p>对接真实渠道时与真实渠道共用同一套 PayChannelStrategy 调用方逻辑，无感知切换。</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mall.pay.mock.enabled", havingValue = "true")
public class MockChannelStrategy implements PayChannelStrategy {

    /** dev/test 开关：mall.pay.mock.enabled，prod 置 false */
    @Value("${mall.pay.mock.enabled:false}")
    private boolean mockEnabled;

    @Override
    public String channelCode() {
        return "MOCK";
    }

    @Override
    public PayPrepayResult prepay(PayOrderEntity order, PayChannelConfigEntity config) {
        if (!mockEnabled) {
            return fail("MOCK 渠道未启用（mall.pay.mock.enabled=false）");
        }
        PayPrepayResult result = new PayPrepayResult();
        result.setChannelCode("MOCK");
        // 模拟拉起支付参数：前端拿到后直接调起 MOCK 收银台，"支付"即成功
        result.setPrepayParams("{\"mock\":true,\"payOrderNo\":\"" + order.getPayOrderNo()
                + "\",\"merchantOrderNo\":\"" + order.getMerchantOrderNo() + "\"}");
        result.setSuccess(true);
        log.info("[MOCK] prepay 成功, payOrderNo={}, merchantOrderNo={}", order.getPayOrderNo(), order.getMerchantOrderNo());
        return result;
    }

    @Override
    public PayQueryResult query(String merchantOrderNo, PayChannelConfigEntity config) {
        PayQueryResult result = new PayQueryResult();
        result.setMerchantOrderNo(merchantOrderNo);
        result.setChannelTradeNo("MOCK" + merchantOrderNo);
        // MOCK 渠道默认模拟支付成功，供主动查单兜底链路联调
        result.setSuccess(true);
        result.setPayAmount(null); // 由调用方从本地 pay_order 读取，不模拟渠道侧金额
        result.setSuccessTime(new Date());
        return result;
    }

    @Override
    public boolean close(String merchantOrderNo, PayChannelConfigEntity config) {
        return true;
    }

    @Override
    public PayRefundResult refund(PayRefundEntity refund, PayChannelConfigEntity config) {
        PayRefundResult result = new PayRefundResult();
        result.setRefundNo(refund.getRefundNo());
        result.setChannelRefundNo("MOCKR" + refund.getRefundNo());
        result.setSuccess(true);
        log.info("[MOCK] refund 成功, refundNo={}, amount={}", refund.getRefundNo(), refund.getRefundAmount());
        return result;
    }

    @Override
    public PayRefundQueryResult queryRefund(String refundNo, PayChannelConfigEntity config) {
        PayRefundQueryResult result = new PayRefundQueryResult();
        result.setRefundNo(refundNo);
        result.setChannelRefundNo("MOCKR" + refundNo);
        result.setStatus("SUCCESS");
        result.setSuccess(true);
        return result;
    }

    @Override
    public boolean verifyNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        // MOCK 无真实验签，一律通过
        return true;
    }

    @Override
    public PayNotifyMessage parseNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        // MOCK 回调报文：前端/测试模拟支付成功时，构造一个 PayNotifyMessage。
        // 若调用方通过 MockNotifyController 传入了 merchantOrderNo，则填入（否则 handleNotify 无法反查支付单）。
        PayNotifyMessage message = new PayNotifyMessage();
        message.setPayStatus(PayStatusEnum.PAYMENT.getValue());
        message.setNotifyType("PAY");
        message.setMerchantOrderNo(params.getOrDefault("merchantOrderNo", null));
        message.setSuccessTime(new Date());
        return message;
    }

    @Override
    public byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config) {
        // 构造内存对账单（CSV 文本），模拟渠道侧已成功交易的记录，供对账链路联调
        StringBuilder sb = new StringBuilder();
        sb.append("trade_no,channel_trade_no,refund_no,trade_time,trade_type,amount,fee,income,trade_status,payer_account\n");
        // 示例行（需按对账日动态生成，此处仅演示结构）
        sb.append("MOCKDEMO001,MOCKTRADE001,,2026-08-03 10:00:00,PAY,10000,60,9940,SUCCESS,user_001\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public List<BillRow> parseBill(byte[] content) {
        // MOCK 对账单解析：按行解析成 BillRow（真实渠道实现各自解析，MOCK 仅演示）
        List<BillRow> rows = new ArrayList<>();
        String text = new String(content, StandardCharsets.UTF_8);
        String[] lines = text.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isBlank()) {
                continue;
            }
            String[] fields = line.split(",");
            if (fields.length < 10) {
                continue;
            }
            BillRow row = new BillRow();
            row.setTradeNo(fields[0]);
            row.setChannelTradeNo(fields[1]);
            row.setRefundNo(fields[2].isEmpty() ? null : fields[2]);
            row.setTradeType(fields[4]);
            row.setAmount(Long.parseLong(fields[5]));
            row.setFee(Long.parseLong(fields[6]));
            row.setIncome(Long.parseLong(fields[7]));
            row.setTradeStatus(fields[8]);
            row.setPayerAccount(fields[9]);
            rows.add(row);
        }
        return rows;
    }

    private PayPrepayResult fail(String errMsg) {
        PayPrepayResult result = new PayPrepayResult();
        result.setChannelCode("MOCK");
        result.setSuccess(false);
        result.setErrMsg(errMsg);
        return result;
    }
}
