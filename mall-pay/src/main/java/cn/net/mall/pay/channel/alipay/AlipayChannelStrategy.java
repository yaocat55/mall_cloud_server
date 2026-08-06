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
import cn.net.mall.pay.support.AlipayClientFactory;
import cn.net.mall.pay.support.BillCsvParser;
import cn.net.mall.pay.support.MoneyUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 支付宝 App 支付渠道策略.
 *
 * <p>基于 alipay-sdk-java 官方 SDK。AlipayClient 由 {@link AlipayClientFactory}
 * 创建并缓存（Caffeine），配置变更时自动淘汰。</p>
 *
 * <p>渠道配置从 pay_channel_config 表读取，经由 {@link cn.net.mall.pay.service.PayChannelService}
 * Caffeine 缓存后传入（支付主流程不查库）。</p>
 *
 * <p>金额换算规则：我方库统一以分存储；支付宝 App 支付 total_amount 单位为元（字符串），
 * 提交时 分 ÷ 100 → 元，回调时反过来。换算只发生在本策略内。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlipayChannelStrategy implements PayChannelStrategy {

    private final AlipayClientFactory alipayClientFactory;

    /** 支付宝网关地址——正式环境 / 沙箱环境从 pay_channel_config 的 config_json 字段读取 */

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
            AlipayClient client = alipayClientFactory.getClient(config);

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
            AlipayClient client = alipayClientFactory.getClient(config);

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
            AlipayClient client = alipayClientFactory.getClient(config);

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
            AlipayClient client = alipayClientFactory.getClient(config);

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
        try {
            AlipayClient client = alipayClientFactory.getClient(config);

            com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest request =
                    new com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest();
            request.setBizContent("{\"out_request_no\":\"" + refundNo + "\"}");

            com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse response = client.execute(request);
            PayRefundQueryResult result = new PayRefundQueryResult();
            result.setRefundNo(refundNo);
            result.setSuccess(response.isSuccess());
            if (response.isSuccess()) {
                // 支付宝 refund_status: REFUND_SUCCESS / REFUND_PROCESSING / REFUND_FAIL
                result.setChannelRefundNo(response.getTradeNo());
                result.setStatus(response.getRefundStatus());
            }
            return result;

        } catch (AlipayApiException e) {
            log.error("[ALIPAY] queryRefund 异常, refundNo={}", refundNo, e);
            PayRefundQueryResult result = new PayRefundQueryResult();
            result.setRefundNo(refundNo);
            result.setSuccess(false);
            return result;
        }
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
    public PayNotifyMessage parseNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config) {
        PayNotifyMessage message = new PayNotifyMessage();
        message.setChannelCode("ALIPAY");
        message.setNotifyType("PAY");
        message.setMerchantOrderNo(params.get("out_trade_no"));
        message.setChannelTradeNo(params.get("trade_no"));

        // 支付状态映射
        String tradeStatus = params.get("trade_status");
        if (TRADE_SUCCESS.equals(tradeStatus)) {
            message.setPayStatus(PayStatusEnum.PAYMENT.getValue());
        } else {
            // 非成功状态：TRADE_CLOSED / WAIT_BUYER_PAY 等，不更新支付状态
            log.info("[ALIPAY] 回调 trade_status={}，非支付成功，仅记录", tradeStatus);
            return message;
        }

        // 成功时间
        String gmtPayment = params.get("gmt_payment");
        if (gmtPayment != null && !gmtPayment.isEmpty()) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                message.setSuccessTime(sdf.parse(gmtPayment));
            } catch (Exception e) {
                message.setSuccessTime(new Date());
            }
        } else {
            message.setSuccessTime(new Date());
        }

        // 金额：支付宝 total_amount 为元（字符串），转分
        String totalAmount = params.get("total_amount");
        if (totalAmount != null && !totalAmount.isEmpty()) {
            message.setPayAmount(MoneyUtil.yuanToFen(totalAmount));
        }

        log.info("[ALIPAY] 回调解析成功, merchantOrderNo={}, channelTradeNo={}, payAmount={}分",
                message.getMerchantOrderNo(), message.getChannelTradeNo(), message.getPayAmount());
        return message;
    }

    @Override
    public byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config) {
        try {
            AlipayClient client = alipayClientFactory.getClient(config);

            // Step 1: 获取账单下载链接（30 秒有效）
            AlipayDataDataserviceBillDownloadurlQueryRequest request =
                    new AlipayDataDataserviceBillDownloadurlQueryRequest();
            request.setBizContent("{" +
                    "\"bill_type\":\"trade\"," +
                    "\"bill_date\":\"" + tradeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\"" +
                    "}");

            com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse response =
                    client.execute(request);

            if (!response.isSuccess()) {
                log.error("[ALIPAY] 获取对账单下载链接失败, tradeDate={}, msg={}, subMsg={}",
                        tradeDate, response.getMsg(), response.getSubMsg());
                return new byte[0];
            }

            String downloadUrl = response.getBillDownloadUrl();
            if (downloadUrl == null || downloadUrl.isEmpty()) {
                log.error("[ALIPAY] 对账单下载链接为空, tradeDate={}", tradeDate);
                return new byte[0];
            }
            log.info("[ALIPAY] 获取对账单下载链接成功, tradeDate={}", tradeDate);

            // Step 2: HTTP GET 下载 csv.zip 文件
            byte[] zipData = httpDownload(downloadUrl);
            if (zipData == null || zipData.length == 0) {
                log.error("[ALIPAY] 对账单下载内容为空, tradeDate={}", tradeDate);
                return new byte[0];
            }

            // Step 3: 解压 zip → 取第一个 CSV 文件内容
            byte[] csvContent = unzipFirstEntry(zipData);
            log.info("[ALIPAY] 对账单下载解压成功, tradeDate={}, size={}bytes", tradeDate, csvContent.length);
            return csvContent;

        } catch (Exception e) {
            log.error("[ALIPAY] 对账单下载异常, tradeDate={}", tradeDate, e);
            return new byte[0];
        }
    }

    @Override
    public List<BillRow> parseBill(byte[] content) {
        if (content == null || content.length == 0) {
            return Collections.emptyList();
        }
        try {
            List<Map<String, String>> rows = BillCsvParser.parse(content, Charset.forName("GBK"));
            List<BillRow> result = new ArrayList<>(rows.size());
            for (Map<String, String> row : rows) {
                BillRow bill = new BillRow();
                bill.setTradeNo(row.get("商户订单号"));
                bill.setChannelTradeNo(row.get("支付宝交易号"));
                bill.setTradeTime(parseDateTime(row.get("完成时间")));
                bill.setPayerAccount(row.get("对方账户"));

                // 业务类型 → tradeType
                String bizType = row.get("业务类型");
                if (bizType != null) {
                    if (bizType.contains("退款")) {
                        bill.setTradeType("REFUND");
                        bill.setRefundNo(row.get("退款批次号/请求号"));
                    } else {
                        bill.setTradeType("PAY");
                    }
                }

                // 金额：元（字符串）→ 分
                bill.setAmount(MoneyUtil.yuanToFen(row.get("订单金额（元）")));
                bill.setIncome(MoneyUtil.yuanToFen(row.get("商家实收（元）")));
                bill.setFee(MoneyUtil.yuanToFen(row.get("服务费（元）")));

                // 留底扩展字段
                bill.setExtJson(extractExtJson(row));

                result.add(bill);
            }
            log.info("[ALIPAY] 对账单解析完成, 共{}行", result.size());
            return result;
        } catch (Exception e) {
            log.error("[ALIPAY] 对账单解析异常", e);
            return Collections.emptyList();
        }
    }

    // ============ private ============

    private PayPrepayResult fail(String errMsg) {
        PayPrepayResult result = new PayPrepayResult();
        result.setChannelCode("ALIPAY");
        result.setSuccess(false);
        result.setErrMsg(errMsg);
        return result;
    }

    /** HTTP GET 下载文件，30 秒超时 */
    private byte[] httpDownload(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[4096];
                int n;
                try (java.io.InputStream is = conn.getInputStream()) {
                    while ((n = is.read(buf)) != -1) {
                        baos.write(buf, 0, n);
                    }
                }
                return baos.toByteArray();
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            log.error("[ALIPAY] HTTP 下载异常, url={}", urlStr, e);
            return new byte[0];
        }
    }

    /** 解压 zip，返回第一个条目的内容 */
    private byte[] unzipFirstEntry(byte[] zipData) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                log.warn("[ALIPAY] zip 内无文件");
                return new byte[0];
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = zis.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("[ALIPAY] zip 解压异常", e);
            return new byte[0];
        }
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

    /** 提取特有字段为 extJson，排除已映射的关键字段 */
    private String extractExtJson(Map<String, String> row) {
        // 简化：排除关键映射字段后，其余 for JSON
        Map<String, String> ext = new java.util.LinkedHashMap<>();
        row.forEach((k, v) -> {
            if (!k.equals("支付宝交易号") && !k.equals("商户订单号")
                    && !k.equals("业务类型") && !k.equals("完成时间")
                    && !k.equals("订单金额（元）") && !k.equals("商家实收（元）")
                    && !k.equals("服务费（元）") && !k.equals("退款批次号/请求号")
                    && !k.equals("对方账户")) {
                ext.put(k, v);
            }
        });
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(ext);
        } catch (Exception e) {
            return "{}";
        }
    }
}
