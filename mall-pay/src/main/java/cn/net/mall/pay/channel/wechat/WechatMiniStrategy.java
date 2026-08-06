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
import cn.net.mall.pay.support.BillCsvParser;
import cn.net.mall.pay.support.MoneyUtil;
import cn.net.mall.pay.support.WechatConfigFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.billdownload.BillDownloadServiceExtension;
import com.wechat.pay.java.service.billdownload.DigestBillEntity;
import com.wechat.pay.java.service.billdownload.model.BillType;
import com.wechat.pay.java.service.billdownload.model.GetTradeBillRequest;
import com.wechat.pay.java.service.billdownload.model.TarType;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 微信小程序支付渠道策略.
 *
 * <p>与 {@link WechatPayStrategy}（App 支付）的关键差异：</p>
 * <ul>
 *   <li>下单接口：{@code POST /v3/pay/transactions/jsapi}</li>
 *   <li>必须传入 {@code payer.openid}</li>
 *   <li>二次签名串：{@code appId\ntimeStamp\nnonceStr\nprepay_id=wx...}（4 行 vs App 的 5 行）</li>
 *   <li>返回签名字段名：{@code paySign}（App 用 {@code sign}）</li>
 *   <li>返回包字段：{@code package: "prepay_id=..."}（App 用 {@code packageValue: "Sign=WXPay"}）</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatMiniStrategy implements PayChannelStrategy {

    private final WechatConfigFactory wechatConfigFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String channelCode() {
        return "WECHAT_MINI";
    }

    @Override
    public PayPrepayResult prepay(PayOrderEntity order, PayChannelConfigEntity config) {
        try {
            JsapiService jsapiService = new JsapiService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            PrepayRequest request = new PrepayRequest();
            request.setAppid(config.getAppId());
            request.setMchid(config.getMerchantId());
            request.setOutTradeNo(order.getMerchantOrderNo());
            request.setDescription(order.getSubject());
            request.setNotifyUrl(config.getNotifyUrl());
            request.setAttach(order.getPayOrderNo());

            // 小程序支付必须传 openid
            if (order.getOpenId() != null && !order.getOpenId().isEmpty()) {
                Payer payer = new Payer();
                payer.setOpenid(order.getOpenId());
                request.setPayer(payer);
            }

            com.wechat.pay.java.service.payments.jsapi.model.Amount amount =
                    new com.wechat.pay.java.service.payments.jsapi.model.Amount();
            amount.setTotal(order.getTotalAmount().intValue());
            amount.setCurrency("CNY");
            request.setAmount(amount);

            PrepayResponse response = jsapiService.prepay(request);
            String prepayId = response.getPrepayId();

            // ========== 小程序二次签名（4 行，不含 partnerId）==========
            String appId = config.getAppId();
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

            String signStr = appId + "\n" + timeStamp + "\n" + nonceStr + "\n" + "prepay_id=" + prepayId;
            String paySign = signWithPrivateKey(signStr, config.getPrivateKey());

            // 注意字段名: paySign 不是 sign, package 不是 packageValue
            Map<String, String> prepayParams = new LinkedHashMap<>();
            prepayParams.put("appId", appId);
            prepayParams.put("timeStamp", timeStamp);
            prepayParams.put("nonceStr", nonceStr);
            prepayParams.put("package", "prepay_id=" + prepayId);
            prepayParams.put("signType", "RSA");
            prepayParams.put("paySign", paySign);

            PayPrepayResult result = new PayPrepayResult();
            result.setChannelCode("WECHAT_MINI");
            result.setPrepayParams(objectMapper.writeValueAsString(prepayParams));
            result.setSuccess(true);
            log.info("[WECHAT_MINI] prepay 成功: payOrderNo={}, merchantOrderNo={}, prepayId={}",
                    order.getPayOrderNo(), order.getMerchantOrderNo(), prepayId);
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_MINI] prepay 异常: payOrderNo={}", order.getPayOrderNo(), e);
            return fail("微信小程序下单异常: " + e.getMessage());
        }
    }

    @Override
    public PayQueryResult query(String merchantOrderNo, PayChannelConfigEntity config) {
        try {
            JsapiService jsapiService = new JsapiService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(config.getMerchantId());
            request.setOutTradeNo(merchantOrderNo);

            Transaction transaction = jsapiService.queryOrderByOutTradeNo(request);
            PayQueryResult result = new PayQueryResult();
            result.setMerchantOrderNo(merchantOrderNo);
            result.setChannelTradeNo(transaction.getTransactionId());
            result.setSuccess(Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState()));

            TransactionAmount amount = transaction.getAmount();
            if (amount != null && amount.getTotal() != null) {
                result.setPayAmount(amount.getTotal().longValue());
            }
            String successTimeStr = transaction.getSuccessTime();
            if (successTimeStr != null && !successTimeStr.isEmpty()) {
                result.setSuccessTime(Date.from(OffsetDateTime.parse(successTimeStr).toInstant()));
            }
            result.setRawResponse(transaction.getTradeState() != null ? transaction.getTradeState().name() : "");
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_MINI] query 异常: merchantOrderNo={}", merchantOrderNo, e);
            return null;
        }
    }

    @Override
    public boolean close(String merchantOrderNo, PayChannelConfigEntity config) {
        try {
            JsapiService jsapiService = new JsapiService.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(config.getMerchantId());
            request.setOutTradeNo(merchantOrderNo);
            jsapiService.closeOrder(request);
            log.info("[WECHAT_MINI] close 成功: merchantOrderNo={}", merchantOrderNo);
            return true;

        } catch (Exception e) {
            log.error("[WECHAT_MINI] close 异常: merchantOrderNo={}", merchantOrderNo, e);
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
            amount.setTotal(Long.valueOf(refund.getRefundAmount()));
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
            log.info("[WECHAT_MINI] refund: refundNo={}, success={}", refund.getRefundNo(), result.isSuccess());
            return result;

        } catch (Exception e) {
            log.error("[WECHAT_MINI] refund 异常: refundNo={}", refund.getRefundNo(), e);
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
            log.error("[WECHAT_MINI] queryRefund 异常: refundNo={}", refundNo, e);
            PayRefundQueryResult result = new PayRefundQueryResult();
            result.setRefundNo(refundNo);
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public boolean verifyNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        return true;
    }

    @Override
    public PayNotifyMessage parseNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        log.warn("[WECHAT_MINI] parseNotify 不应直接调用，请通过 WechatNotifyController 使用 parseNotifyWithHeaders");
        return null;
    }

    /**
     * 解析微信小程序回调通知（与 App 共用同一套解析逻辑）.
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
                log.warn("[WECHAT_MINI] 回调解析为空");
                return null;
            }

            PayNotifyMessage message = new PayNotifyMessage();
            message.setChannelCode("WECHAT_MINI");
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

            log.info("[WECHAT_MINI] 回调解析成功: merchantOrderNo={}, channelTradeNo={}, payAmount={}分",
                    message.getMerchantOrderNo(), message.getChannelTradeNo(), message.getPayAmount());
            return message;

        } catch (Exception e) {
            log.error("[WECHAT_MINI] 回调解析异常", e);
            return null;
        }
    }

    @Override
    public byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config) {
        try {
            BillDownloadServiceExtension billService = new BillDownloadServiceExtension.Builder()
                    .config(wechatConfigFactory.getConfig(config))
                    .build();

            GetTradeBillRequest getTradeBillRequest = new GetTradeBillRequest();
            getTradeBillRequest.setBillDate(tradeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            getTradeBillRequest.setBillType(BillType.ALL);
            getTradeBillRequest.setTarType(TarType.GZIP);

            DigestBillEntity digest = billService.getTradeBill(getTradeBillRequest);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            try (java.io.InputStream is = digest.getInputStream()) {
                while ((n = is.read(buf)) != -1) {
                    baos.write(buf, 0, n);
                }
            }

            byte[] csvContent = baos.toByteArray();
            log.info("[WECHAT_MINI] 对账单下载成功: tradeDate={}, size={}bytes", tradeDate, csvContent.length);
            return csvContent;

        } catch (Exception e) {
            log.error("[WECHAT_MINI] 对账单下载异常, tradeDate={}", tradeDate, e);
            return new byte[0];
        }
    }

    @Override
    public List<BillRow> parseBill(byte[] content) {
        if (content == null || content.length == 0) {
            return Collections.emptyList();
        }
        try {
            List<Map<String, String>> rows = BillCsvParser.parse(content, StandardCharsets.UTF_8);
            List<BillRow> result = new ArrayList<>(rows.size());
            for (Map<String, String> row : rows) {
                BillRow bill = new BillRow();
                bill.setTradeNo(row.get("商户订单号"));
                bill.setChannelTradeNo(row.get("微信订单号"));
                bill.setTradeTime(parseDateTime(row.get("交易时间")));
                bill.setTradeStatus(row.get("交易状态"));
                bill.setPayerAccount(row.get("用户标识"));

                String tradeStatus = row.get("交易状态");
                if ("REFUND".equals(tradeStatus)) {
                    bill.setTradeType("REFUND");
                } else {
                    bill.setTradeType("PAY");
                }
                bill.setRefundNo(row.get("微信退款单号"));

                bill.setAmount(MoneyUtil.yuanToFen(row.get("应结订单金额（元）")));
                bill.setFee(MoneyUtil.yuanToFen(row.get("手续费（元）")));

                Long amount = bill.getAmount() != null ? bill.getAmount() : 0L;
                Long fee = bill.getFee() != null ? bill.getFee() : 0L;
                bill.setIncome(amount - fee);

                bill.setExtJson(extractExtJson(row));
                result.add(bill);
            }
            log.info("[WECHAT_MINI] 对账单解析完成, 共{}行", result.size());
            return result;
        } catch (Exception e) {
            log.error("[WECHAT_MINI] 对账单解析异常", e);
            return Collections.emptyList();
        }
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
            privateKey = keyFactory.generatePrivate(decodePKCS1(keyBytes));
        }

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signStr.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sig.sign());
    }

    private static java.security.spec.RSAPrivateCrtKeySpec decodePKCS1(byte[] der) throws Exception {
        int pos = 0;
        if (der[pos++] != 0x30) throw new IllegalArgumentException("expected SEQUENCE");
        int seqLen = der[pos++] & 0xff;
        if (seqLen > 128) {
            int lenBytes = seqLen & 0x7f;
            seqLen = 0;
            for (int i = 0; i < lenBytes; i++) seqLen = (seqLen << 8) | (der[pos++] & 0xff);
        }
        pos += skipInnerTag(der, pos); // skip INTEGER
        java.math.BigInteger mod = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger pubExp = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger privExp = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger prime1 = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger prime2 = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger exp1 = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger exp2 = readDERInt(der, ref(pos)); pos = derIntEnd;
        java.math.BigInteger crt = readDERInt(der, ref(pos));
        return new java.security.spec.RSAPrivateCrtKeySpec(mod, pubExp, privExp, prime1, prime2, exp1, exp2, crt);
    }

    private static int derIntEnd;
    private static final int[] derRef = new int[1];

    private static int[] ref(int p) { derRef[0] = p; return derRef; }

    private static java.math.BigInteger readDERInt(byte[] der, int[] posRef) {
        int p = posRef[0];
        if (der[p] != 0x02) throw new IllegalArgumentException("expected INTEGER at " + p);
        int len = der[p + 1] & 0xff;
        byte[] bytes = new byte[len];
        System.arraycopy(der, p + 2, bytes, 0, len);
        posRef[0] = p + 2 + len;
        return new java.math.BigInteger(1, bytes);
    }

    private static int skipInnerTag(byte[] der, int pos) {
        pos++;
        int len = der[pos++] & 0xff;
        if (len > 128) { int lb = len & 0x7f; len = 0; for (int i = 0; i < lb; i++) len = (len << 8) | (der[pos++] & 0xff); }
        return pos + len - 1; // skip content, return new position
    }

    /** 解析日期时间字符串（格式 yyyy-MM-dd HH:mm:ss） */
    private Date parseDateTime(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    /** 提取特有字段为 extJson，排除已映射字段 */
    private String extractExtJson(Map<String, String> row) {
        Map<String, String> ext = new LinkedHashMap<>();
        row.forEach((k, v) -> {
            if (!k.equals("微信订单号") && !k.equals("商户订单号")
                    && !k.equals("交易状态") && !k.equals("交易时间")
                    && !k.equals("应结订单金额（元）") && !k.equals("手续费（元）")
                    && !k.equals("用户标识") && !k.equals("微信退款单号")) {
                ext.put(k, v);
            }
        });
        try {
            return objectMapper.writeValueAsString(ext);
        } catch (Exception e) {
            return "{}";
        }
    }

    private PayPrepayResult fail(String errMsg) {
        PayPrepayResult result = new PayPrepayResult();
        result.setChannelCode("WECHAT_MINI");
        result.setSuccess(false);
        result.setErrMsg(errMsg);
        return result;
    }
}
