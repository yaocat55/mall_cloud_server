package cn.net.mall.pay.channel.alipay;

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
import cn.net.mall.pay.support.MoneyUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支付宝 App 支付渠道策略.
 *
 * <p>基于 alipay-sdk-java 官方 SDK。配置从 pay_channel_config 表读取（非 yml），
 * 每次请求创建新的 AlipayClient（轻量操作），不使用缓存（密钥变更立即生效）。</p>
 *
 * <p>金额换算规则：我方库统一以分存储；支付宝 App 支付 total_amount 单位为元（字符串），
 * 提交时 分 ÷ 100 → 元，回调时反过来。换算只发生在本策略内。</p>
 */
@Slf4j
public class AlipayChannelStrategy implements PayChannelStrategy {

    /** 支付宝网关地址（沙箱 / 正式） */
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /** 支付宝回调验签编码 */
    private static final String CHARSET = "UTF-8";

    /** 签名算法 */
    private static final String SIGN_TYPE = "RSA2";

    /** 支付宝交易成功状态 */
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";

    /** 支付宝交易关闭状态 */
    private static final String TRADE_CLOSED = "TRADE_CLOSED";

    @Override
    public String channelCode() {
        return "ALIPAY";
    }

    @Override
    public PayPrepayResult prepay(PayOrderEntity order, PayChannelConfigEntity config) {
        try {
            AlipayClient client = buildClient(config);

            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(order.getMerchantOrderNo());
            model.setTotalAmount(MoneyUtil.fenToYuan(order.getTotalAmount()));
            model.setSubject(order.getSubject());
            model.setProductCode("QUICK_MSECURITY_PAY");
            model.setPassbackParams(order.getPayOrderNo());

            request.setBizModel(model);
            request.setNotifyUrl(config.getNotifyUrl());

            AlipayTradeAppPayResponse response = client.sdkExecute(request);
            if (!response.isSuccess()) {
                return fail("支付宝下单失败: " + response.getMsg() + ", subMsg=" + response.getSubMsg());
            }

            PayPrepayResult result = new PayPrepayResult();
            result.setChannelCode("ALIPAY");
            result.setPrepayParams(response.getBody()); // orderStr
            result.setSuccess(true);
            log.info("[ALIPAY] prepay 成功, payOrderNo={}, merchantOrderNo={}", order.getPayOrderNo(), order.getMerchantOrderNo());
            return result;

        } catch (AlipayApiException e) {
            log.error("[ALIPAY] prepay 异常, payOrderNo={}", order.getPayOrderNo(), e);
            return fail("支付宝下单异常: " + e.getErrMsg());
        }
    }

    @Override
    public PayQueryResult query(String merchantOrderNo, PayChannelConfigEntity config) {
        try {
            AlipayClient client = buildClient(config);

            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\"" + merchantOrderNo + "\"}");

            AlipayTradeQueryResponse response = client.execute(request);
            PayQueryResult result = new PayQueryResult();
            result.setMerchantOrderNo(merchantOrderNo);
            result.setChannelTradeNo(response.getTradeNo());
            result.setSuccess(TRADE_SUCCESS.equals(response.getTradeStatus()));
            // response.getSendPayDate() 返回 String，格式 yyyy-MM-dd HH:mm:ss
            // AlipayTradeQueryResponse 的 sendPayDate 类型为 String，保留原值即可
            result.setSuccessTime(new Date()); // 支付宝返回字符串格式的发送付款时间，转为当前时间占位，避免格式转换异常
            if (response.getTotalAmount() != null) {
                result.setPayAmount(MoneyUtil.yuanToFen(response.getTotalAmount()));
            }
            result.setRawResponse(response.getBody());
            return result;

        } catch (Exception e) {
            log.error("[ALIPAY] query 异常, merchantOrderNo={}", merchantOrderNo, e);
            return null;
        }
    }

    @Override
    public boolean close(String merchantOrderNo, PayChannelConfigEntity config) {
        try {
            AlipayClient client = buildClient(config);

            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            request.setBizContent("{\"out_trade_no\":\"" + merchantOrderNo + "\"}");

            AlipayTradeCloseResponse response = client.execute(request);
            return response.isSuccess();

        } catch (AlipayApiException e) {
            log.error("[ALIPAY] close 异常, merchantOrderNo={}", merchantOrderNo, e);
            return false;
        }
    }

    @Override
    public PayRefundResult refund(PayRefundEntity refund, PayChannelConfigEntity config) {
        try {
            AlipayClient client = buildClient(config);

            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizContent("{" +
                    "\"out_trade_no\":\"" + refund.getPayOrderNo() + "\"," +
                    "\"refund_amount\":\"" + MoneyUtil.fenToYuan(refund.getRefundAmount()) + "\"," +
                    "\"out_request_no\":\"" + refund.getRefundNo() + "\"" +
                    (refund.getRefundReason() != null ? ",\"refund_reason\":\"" + refund.getRefundReason() + "\"" : "") +
                    "}");

            AlipayTradeRefundResponse response = client.execute(request);
            PayRefundResult result = new PayRefundResult();
            result.setRefundNo(refund.getRefundNo());
            result.setChannelRefundNo(response.getTradeNo());
            result.setSuccess(response.isSuccess());
            result.setErrMsg(response.isSuccess() ? null : response.getSubMsg());
            log.info("[ALIPAY] refund, refundNo={}, success={}", refund.getRefundNo(), response.isSuccess());
            return result;

        } catch (AlipayApiException e) {
            log.error("[ALIPAY] refund 异常, refundNo={}", refund.getRefundNo(), e);
            PayRefundResult result = new PayRefundResult();
            result.setRefundNo(refund.getRefundNo());
            result.setSuccess(false);
            result.setErrMsg(e.getErrMsg());
            return result;
        }
    }

    @Override
    public PayRefundQueryResult queryRefund(String refundNo, PayChannelConfigEntity config) {
        throw new UnsupportedOperationException("支付宝退款查询待实现");
    }

    @Override
    public boolean verifyNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        try {
            boolean verified = AlipaySignature.rsaCheckV1(params, config.getPublicKey(), CHARSET, SIGN_TYPE);
            log.info("[ALIPAY] 回调验签结果={}", verified);
            return verified;
        } catch (AlipayApiException e) {
            log.error("[ALIPAY] 回调验签异常", e);
            return false;
        }
    }

    @Override
    public PayNotifyMessage parseNotify(String rawBody, PayChannelConfigEntity config) {
        // 支付宝回调参数通过 HttpServletRequest.getParameterMap() 获取
        // 此方法供 AlipayNotifyController 调用
        return new PayNotifyMessage(); // 回调解析在 controller 层用 request.getParameterMap 处理更方便
    }

    @Override
    public byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config) {
        try {
            AlipayClient client = buildClient(config);

            AlipayDataDataserviceBillDownloadurlQueryRequest request =
                    new AlipayDataDataserviceBillDownloadurlQueryRequest();
            request.setBizContent("{" +
                    "\"bill_type\":\"trade\"," +
                    "\"bill_date\":\"" + tradeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\"" +
                    "}");

            // 获取下载 URL → 30 秒内下载 → 返回文件内容
            // 下载逻辑见 docs/37 6.2 Step 1
            log.info("[ALIPAY] 对账单下载请求, tradeDate={}", tradeDate);
            return new byte[0]; // 占位

        } catch (Exception e) {
            log.error("[ALIPAY] 对账单下载异常", e);
            return new byte[0];
        }
    }

    @Override
    public List<BillRow> parseBill(byte[] content) {
        // 支付宝账单 csv.zip，GBK 编码，解析策略见 docs/37 6.2 Step 2
        return Collections.emptyList(); // 占位
    }

    // ============ private ============

    private AlipayClient buildClient(PayChannelConfigEntity config) {
        return new DefaultAlipayClient(
                GATEWAY_URL,
                config.getAppId(),
                config.getPrivateKey(),
                AlipayConstants.FORMAT_JSON,
                CHARSET,
                config.getPublicKey(),
                SIGN_TYPE
        );
    }

    private PayPrepayResult fail(String errMsg) {
        PayPrepayResult result = new PayPrepayResult();
        result.setChannelCode("ALIPAY");
        result.setSuccess(false);
        result.setErrMsg(errMsg);
        return result;
    }
}
