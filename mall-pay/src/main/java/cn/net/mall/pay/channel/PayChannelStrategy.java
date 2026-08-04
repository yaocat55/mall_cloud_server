package cn.net.mall.pay.channel;

import cn.net.mall.pay.dto.BillRow;
import cn.net.mall.pay.dto.PayNotifyMessage;
import cn.net.mall.pay.dto.PayPrepayResult;
import cn.net.mall.pay.dto.PayQueryResult;
import cn.net.mall.pay.dto.PayRefundResult;
import cn.net.mall.pay.dto.PayRefundQueryResult;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.entity.PayRefundEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 支付渠道策略接口.
 *
 * <p>每个渠道（支付宝/微信支付/微信小程序/MOCK）实现一个策略，新增渠道只需新增一个实现 + 一条渠道配置。</p>
 * <p>金额统一以「分」为单位（bigint），元→分换算只发生在各渠道策略内部。</p>
 */
public interface PayChannelStrategy {

    /**
     * 渠道编码：ALIPAY / WECHAT_PAY / WECHAT_MINI / MOCK
     */
    String channelCode();

    /**
     * 创建支付单（merchant_order_no 幂等），返回拉起支付参数（调起 SDK 的 json/字符串）
     */
    PayPrepayResult prepay(PayOrderEntity order, PayChannelConfigEntity config);

    /**
     * 主动查询订单状态（兜底轮询用）
     */
    PayQueryResult query(String merchantOrderNo, PayChannelConfigEntity config);

    /**
     * 关闭未支付订单
     */
    boolean close(String merchantOrderNo, PayChannelConfigEntity config);

    /**
     * 申请退款（out_refund_no 幂等；超时后禁止自动重试，先查单）
     */
    PayRefundResult refund(PayRefundEntity refund, PayChannelConfigEntity config);

    /**
     * 查询退款结果
     */
    PayRefundQueryResult queryRefund(String refundNo, PayChannelConfigEntity config);

    /**
     * 回调报文验签
     */
    boolean verifyNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config);

    /**
     * 解析回调报文为统一对象.
     *
     * @param params  回调请求参数（支付宝为 request parameter map，微信为解密后的 XML/JSON map）
     * @param rawBody 回调原始报文
     * @param config  渠道配置
     */
    PayNotifyMessage parseNotify(Map<String, String> params, String rawBody, PayChannelConfigEntity config);

    /**
     * 下载对账单（30 秒链接原子下载），返回文件内容
     */
    byte[] downloadBill(LocalDate tradeDate, PayChannelConfigEntity config);

    /**
     * 解析对账单 → 统一账单行结构
     */
    List<BillRow> parseBill(byte[] content);
}
