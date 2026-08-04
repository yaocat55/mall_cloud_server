package cn.net.mall.pay.channel.wechat;

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
import cn.net.mall.pay.support.WechatConfigFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.app.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayResponse;
import com.wechat.pay.java.service.payments.app.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.TransactionAmount;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 微信 App 支付渠道策略.
 *
 * <p>基于官方 {@code wechatpay-java} SDK. SDK 负责 APIv3 签名 + 平台证书管理,
 * 本策略负责 prepay_id 的二次签名（SHA256withRSA).</p>
 *
 * <p>金额：微信 APIv3 金额单位为分，与我方库一致，无需换算.</p>
 *
 * <p>二次签名：SDK 的 prepay 返回 prepay_id，前端调起 SDK 需要服务端拼签名串（5 行）
 * + SHA256withRSA 签名，返回 {appId, partnerId, prepayId, nonceStr, timeStamp, packageValue, sign}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPayStrategy implements PayChannelStrategy {

    private final WechatConfigFactory wechatConfigFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String channelCode() {
        return "WECHAT_PAY";
    }

    @Override
    public PayPrepayResult prepay(PayOrderEntity order, PayChannelConfigEntity config) {
        try {
            AppService appService = new AppService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            PrepayRequest request = new PrepayRequest();
            request.setAppid(config.getAppId());
            request.setMchid(config.getMerchantId());
            request.setOutTradeNo(order.getMerchantOrderNo());
            request.setDescription(order.getSubject());
            request.setNotifyUrl(config.getNotifyUrl());
            request.setAttach(order.getPayOrderNo());

            com.wechat.pay.java.service.payments.app.model.Amount amount =
                    new com.wechat.pay.java.service.payments.app.model.Amount();
            amount.setTotal(order.getTotalAmount().intValue());
            amount.setCurrency("CNY");
            request.setAmount(amount);

            PrepayResponse response = appService.prepay(request);
            String prepayId = response.getPrepayId();

            // ========== 二次签名（App 格式：5 行）==========
            String appId = config.getAppId();
            String partnerId = config.getMerchantId();
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

            String signStr = appId + "\n" + partnerId + "\n" + prepayId + "\n" + nonceStr + "\n" + timeStamp;
            String sign = signWithPrivateKey(signStr, config.getPrivateKey());

            Map<String, String> prepayParams = new LinkedHashMap<>();
            prepayParams.put("appId", appId);
            prepayParams.put("partnerId", partnerId);
            prepayParams.put("prepayId", prepayId);
            prepayParams.put("nonceStr", nonceStr);
            prepayParams.put("timeStamp", timeStamp);
            prepayParams.put("packageValue", "Sign=WXPay");
            prepayParams.put("sign", sign);

            PayPrepayResult result = new PayPrepayResult();
            result.setChannelCode("WECHAT_PAY");
            result.setPrepayParams(objectMapper.writeValueAsString(prepayParams));
            result.setSuccess(true);
            log.info("[WECHAT_PAY] prepay 成功: payOrderNo={}, merchantOrderNo={}, prepayId={}",
                    order.getPayOrderNo(), order.getMerchantOrderNo(), prepayId);
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_PAY] prepay 异常: payOrderNo={}", order.getPayOrderNo(), e);
            return fail("微信下单异常: " + e.getMessage());
        }
    }

    @Override
    public PayQueryResult query(String merchantOrderNo, PayChannelConfigEntity config) {
        try {
            AppService appService = new AppService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(config.getMerchantId());
            request.setOutTradeNo(merchantOrderNo);

            Transaction transaction = appService.queryOrderByOutTradeNo(request);
            PayQueryResult result = new PayQueryResult();
            result.setMerchantOrderNo(merchantOrderNo);
            result.setChannelTradeNo(transaction.getTransactionId());
            result.setSuccess(Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState()));

            TransactionAmount amount = transaction.getAmount();
            if (amount != null && amount.getTotal() != null) {
                result.setPayAmount(amount.getTotal().longValue());
            }
            // successTime is RFC3339 string (e.g. "2026-08-03T10:00:05+08:00")
            String successTimeStr = transaction.getSuccessTime();
            if (successTimeStr != null && !successTimeStr.isEmpty()) {
                result.setSuccessTime(Date.from(OffsetDateTime.parse(successTimeStr).toInstant()));
            }
            result.setRawResponse(transaction.getTradeState() != null ? transaction.getTradeState().name() : "");
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_PAY] query 异常: merchantOrderNo={}", merchantOrderNo, e);
            return null;
        }
    }

    @Override
    public boolean close(String merchantOrderNo, PayChannelConfigEntity config) {
        try {
            AppService appService = new AppService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(config.getMerchantId());
            request.setOutTradeNo(merchantOrderNo);
            appService.closeOrder(request);
            log.info("[WECHAT_PAY] close 成功: merchantOrderNo={}", merchantOrderNo);
            return true;

        } catch (Exception e) {
            log.error("[WECHAT_PAY] close 异常: merchantOrderNo={}", merchantOrderNo, e);
            return false;
        }
    }

    @Override
    public PayRefundResult refund(PayRefundEntity refund, PayChannelConfigEntity config) {
        try {
            RefundService refundService = new RefundService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            CreateRequest request = new CreateRequest();
            request.setOutTradeNo(refund.getPayOrderNo());
            request.setOutRefundNo(refund.getRefundNo());

            AmountReq amount = new AmountReq();
            amount.setRefund(Long.valueOf(refund.getRefundAmount()));
            amount.setTotal(Long.valueOf(refund.getRefundAmount())); // 简化：原单金额暂取退款金额
            amount.setCurrency("CNY");
            request.setAmount(amount);

            if (refund.getRefundReason() != null && !refund.getRefundReason().isEmpty()) {
                request.setReason(refund.getRefundReason());
            }

            com.wechat.pay.java.service.refund.model.Refund response = refundService.create(request);
            PayRefundResult result = new PayRefundResult();
            result.setRefundNo(refund.getRefundNo());
            result.setChannelRefundNo(response.getRefundId());
            result.setSuccess(Status.SUCCESS.equals(response.getStatus()));
            log.info("[WECHAT_PAY] refund: refundNo={}, success={}", refund.getRefundNo(), result.isSuccess());
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_PAY] refund 异常: refundNo={}", refund.getRefundNo(), e);
            PayRefundResult result = new PayRefundResult();
            result.setRefundNo(refund.getRefundNo());
            result.setSuccess(false);
            result.setErrMsg(e.getMessage());
            return result;
        }
    }

    @Override
    public PayRefundQueryResult queryRefund(String refundNo, PayChannelConfigEntity config) {
        try {
            RefundService refundService = new RefundService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
            request.setOutRefundNo(refundNo);

            com.wechat.pay.java.service.refund.model.Refund response = refundService.queryByOutRefundNo(request);
            PayRefundQueryResult result = new PayRefundQueryResult();
            result.setRefundNo(refundNo);
            result.setChannelRefundNo(response.getRefundId());
            result.setStatus(response.getStatus() != null ? response.getStatus().name() : "");
            result.setSuccess(Status.SUCCESS.equals(response.getStatus()));
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_PAY] queryRefund 异常: refundNo={}", refundNo, e);
            PayRefundQueryResult result = new PayRefundQueryResult();
            result.setRefundNo(refundNo);
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public boolean verifyNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        // 验签合并到 parseNotifyWithHeaders（微信 SDK 的 NotificationParser 验签+解密是一体的）
        return true;
    }

    @Override
    public PayNotifyMessage parseNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        log.warn("[WECHAT_PAY] parseNotify 不应直接调用，请通过 WechatNotifyController 使用 parseNotifyWithHeaders");
        return null;
    }

    /**
     * 解析微信回调通知（由 WechatNotifyController 调用）.
     */
    public PayNotifyMessage parseNotifyWithHeaders(String wechatpaySignature, String wechatpayTimestamp,
                                                    String wechatpayNonce, String wechatpaySerial,
                                                    String rawBody, PayChannelConfigEntity config) {
        try {
            NotificationParser parser = new NotificationParser(
                    (com.wechat.pay.java.core.notification.NotificationConfig)
                            wechatConfigFactory.getConfig(config));

            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(wechatpaySerial)
                    .nonce(wechatpayNonce)
                    .timestamp(wechatpayTimestamp)
                    .signature(wechatpaySignature)
                    .body(rawBody)
                    .build();

            Transaction transaction = parser.parse(requestParam, Transaction.class);

            if (transaction == null) {
                log.warn("[WECHAT_PAY] 回调解析为空");
                return null;
            }

            PayNotifyMessage message = new PayNotifyMessage();
            message.setChannelCode("WECHAT_PAY");
            message.setNotifyType("PAY");
            message.setMerchantOrderNo(transaction.getOutTradeNo());
            message.setChannelTradeNo(transaction.getTransactionId());

            if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                message.setPayStatus(PayStatusEnum.PAYMENT.getValue());
            }

            TransactionAmount amount = transaction.getAmount();
            if (amount != null && amount.getTotal() != null) {
                message.setPayAmount(amount.getTotal().longValue());
            }
            String successTimeStr = transaction.getSuccessTime();
            if (successTimeStr != null && !successTimeStr.isEmpty()) {
                message.setSuccessTime(Date.from(OffsetDateTime.parse(successTimeStr).toInstant()));
            } else {
                message.setSuccessTime(new Date());
            }

            log.info("[WECHAT_PAY] 回调解析成功: merchantOrderNo={}, channelTradeNo={}, payAmount={}分",
                    message.getMerchantOrderNo(), message.getChannelTradeNo(), message.getPayAmount());
            return message;

        } catch (Exception e) {
            log.error("[WECHAT_PAY] 回调解析异常", e);
            return null;
        }
    }

    @Override
    public byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config) {
        log.info("[WECHAT_PAY] 对账单下载请求: tradeDate={}（占位）", tradeDate);
        return new byte[0];
    }

    @Override
    public List<BillRow> parseBill(byte[] content) {
        return Collections.emptyList();
    }

    // ============ private ============

    private String signWithPrivateKey(String signStr, String privateKeyPem) throws Exception {
        String keyContent = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey;
        try {
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            // PKCS8 失败时 fallback PKCS1
            privateKey = keyFactory.generatePrivate(decodePKCS1(keyBytes));
        }

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signStr.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sig.sign());
    }

    private static java.security.spec.RSAPrivateCrtKeySpec decodePKCS1(byte[] der) throws Exception {
        int pos = 0;
        // SEQUENCE
        if (der[pos++] != 0x30) throw new IllegalArgumentException("expected SEQUENCE");
        int seqLen = der[pos++] & 0xff;
        if (seqLen > 128) {
            int lenBytes = seqLen & 0x7f;
            seqLen = 0;
            for (int i = 0; i < lenBytes; i++) seqLen = (seqLen << 8) | (der[pos++] & 0xff);
        }
        // INTEGER version
        pos += skipTag(der, pos);
        java.math.BigInteger mod = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger pubExp = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger privExp = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger prime1 = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger prime2 = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger exp1 = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger exp2 = readBigInt(der, pos); pos = readBigIntEnd;
        java.math.BigInteger crt = readBigInt(der, pos);
        return new java.security.spec.RSAPrivateCrtKeySpec(mod, pubExp, privExp, prime1, prime2, exp1, exp2, crt);
    }

    private static int readBigIntEnd;

    private static java.math.BigInteger readBigInt(byte[] der, int pos) {
        if (der[pos] != 0x02) throw new IllegalArgumentException("expected INTEGER at " + pos);
        int len = der[pos + 1] & 0xff;
        int start = pos + 2;
        readBigIntEnd = start + len;
        byte[] bytes = new byte[len];
        System.arraycopy(der, start, bytes, 0, len);
        return new java.math.BigInteger(1, bytes);
    }

    private static int skipTag(byte[] der, int pos) {
        pos++; // skip tag
        int len = der[pos++] & 0xff;
        if (len > 128) { int lb = len & 0x7f; len = 0; for (int i = 0; i < lb; i++) len = (len << 8) | (der[pos++] & 0xff); }
        return pos + len - pos + 1; // return bytes consumed
    }

    private PayPrepayResult fail(String errMsg) {
        PayPrepayResult result = new PayPrepayResult();
        result.setChannelCode("WECHAT_PAY");
        result.setSuccess(false);
        result.setErrMsg(errMsg);
        return result;
    }
}
