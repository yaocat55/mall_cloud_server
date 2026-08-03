package cn.net.mall.pay.service;

import cn.net.mall.pay.dto.PayRefundDTO;
import cn.net.mall.pay.dto.PayRefundQueryResult;
import cn.net.mall.pay.dto.PayRefundResult;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.entity.PayRefundConditionEntity;
import cn.net.mall.pay.entity.PayRefundEntity;
import cn.net.mall.pay.enums.PayStatusEnum;
import cn.net.mall.pay.enums.RefundAuditStatusEnum;
import cn.net.mall.pay.enums.RefundStatusEnum;
import cn.net.mall.pay.support.IdGenerator;
import cn.net.mall.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 退款核心服务.
 *
 * <p>主线二：退款申请 → 审核判定 → 渠道退款 → 回调 → 冲正累计。</p>
 * <p>审核规则（已确认）：退款请求的 biz_order_no 或 user_id 在 pay_order 查得到 → 自动退款；
 * 查不到 → 人工审核（防伪造退款）。</p>
 */
@Slf4j
@Service
public class RefundCoreService {

    @Autowired
    private PayRefundService payRefundService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 申请退款.
     */
    @Transactional(rollbackFor = Exception.class)
    public PayRefundResult apply(PayRefundDTO dto) {
        // 1. 校验：支付单已支付、累计已退 + 本次 ≤ pay_amount
        PayOrderEntity payOrder = findPayOrder(dto.getPayOrderNo());
        AssertUtil.notNull(payOrder, "支付订单不存在: " + dto.getPayOrderNo());
        AssertUtil.isTrue(PayStatusEnum.PAYMENT.getValue().equals(payOrder.getPayStatus()),
                "支付单未支付，不能退款: " + dto.getPayOrderNo());
        long refunded = payOrder.getRefundAmount() == null ? 0L : payOrder.getRefundAmount();
        AssertUtil.isTrue(refunded + dto.getRefundAmount() <= payOrder.getPayAmount(),
                "退款金额超过可退金额");

        // 2. 审核判定：biz_order_no 或 user_id 在 pay_order 查得到 → 自动退款；查不到 → 人工审核
        boolean autoAudit = findPayOrderByBizOrder(dto.getBizType(), dto.getBizOrderNo()) != null
                && payOrder.getUserId().equals(dto.getUserId());
        Integer auditStatus = autoAudit
                ? RefundAuditStatusEnum.NONE.getValue()
                : RefundAuditStatusEnum.WAIT_AUDIT.getValue();

        // 3. 创建 pay_refund
        PayRefundEntity refund = new PayRefundEntity();
        refund.setId(idGenerator.nextId());
        refund.setRefundNo(idGenerator.nextRefundNo());
        refund.setPayOrderId(payOrder.getId());
        refund.setPayOrderNo(payOrder.getPayOrderNo());
        refund.setBizOrderNo(dto.getBizOrderNo());
        refund.setBizType(dto.getBizType());
        refund.setUserId(dto.getUserId());
        refund.setChannelCode(payOrder.getChannelCode());
        refund.setRefundAmount(dto.getRefundAmount());
        refund.setRefundFee(0L);
        refund.setRefundReason(dto.getRefundReason());
        refund.setRefundType(dto.getRefundType() == null ? 1 : dto.getRefundType());
        refund.setAuditStatus(auditStatus);
        refund.setRefundStatus(RefundStatusEnum.PENDING.getValue());
        refund.setVersion(0);
        payRefundService.insert(refund);

        // 4. 自动退款 → 直接提交渠道
        PayRefundResult result = new PayRefundResult();
        result.setRefundNo(refund.getRefundNo());
        if (autoAudit) {
            submitToChannel(refund, payOrder.getChannelCode());
        } else {
            log.info("退款进入人工审核, refundNo={}, bizOrderNo={}", refund.getRefundNo(), dto.getBizOrderNo());
            result.setSuccess(false);
            result.setErrMsg("退款需人工审核");
        }
        return result;
    }

    /**
     * 提交渠道退款（自动退款/审核通过后调用）.
     */
    @Transactional(rollbackFor = Exception.class)
    public PayRefundResult submitToChannel(PayRefundEntity refund, String channelCode) {
        PayChannelConfigEntity config = payChannelService.getConfig(channelCode);
        refund.setRefundStatus(RefundStatusEnum.PROCESSING.getValue());
        payRefundService.update(refund);

        cn.net.mall.pay.dto.PayRefundResult channelResult = payChannelService.getStrategy(channelCode)
                .refund(refund, config);
        PayRefundResult result = new PayRefundResult();
        result.setRefundNo(refund.getRefundNo());
        if (channelResult.isSuccess()) {
            refund.setChannelRefundNo(channelResult.getChannelRefundNo());
            refund.setRefundStatus(RefundStatusEnum.SUCCESS.getValue());
            refund.setSuccessTime(new java.util.Date());
            payRefundService.update(refund);
            // 冲正累计：更新 pay_order.refund_amount + refund_status 维度
            accumulateRefund(refund);
            result.setSuccess(true);
        } else {
            refund.setRefundStatus(RefundStatusEnum.FAILED.getValue());
            refund.setFailReason(channelResult.getErrMsg());
            payRefundService.update(refund);
            result.setSuccess(false);
            result.setErrMsg(channelResult.getErrMsg());
        }
        return result;
    }

    /**
     * 退款成功冲正：更新支付单已退金额累计.
     */
    private void accumulateRefund(PayRefundEntity refund) {
        PayOrderEntity payOrder = findPayOrder(refund.getPayOrderNo());
        if (payOrder == null) {
            return;
        }
        long refunded = payOrder.getRefundAmount() == null ? 0L : payOrder.getRefundAmount();
        payOrder.setRefundAmount(refunded + refund.getRefundAmount());
        // refund_status 维度：全额退 → 3 全额退款；否则 2 部分退款
        if (payOrder.getRefundAmount().equals(payOrder.getPayAmount())) {
            payOrder.setRefundStatus(3);
        } else {
            payOrder.setRefundStatus(2);
        }
        payOrderService.update(payOrder);
    }

    /**
     * 查询退款结果.
     */
    public PayRefundQueryResult queryRefund(String refundNo) {
        PayRefundConditionEntity condition = new PayRefundConditionEntity();
        condition.setRefundNo(refundNo);
        List<PayRefundEntity> refunds = payRefundService.searchByPage(condition).getData();
        AssertUtil.notEmpty(refunds, "退款单不存在: " + refundNo);
        PayRefundEntity refund = refunds.get(0);

        PayRefundQueryResult result = new PayRefundQueryResult();
        result.setRefundNo(refund.getRefundNo());
        result.setChannelRefundNo(refund.getChannelRefundNo());
        result.setStatus(RefundStatusEnum.from(refund.getRefundStatus()) != null
                ? RefundStatusEnum.from(refund.getRefundStatus()).getDesc() : null);
        result.setSuccess(RefundStatusEnum.SUCCESS.getValue().equals(refund.getRefundStatus()));
        return result;
    }

    private PayOrderEntity findPayOrder(String payOrderNo) {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setPayOrderNo(payOrderNo);
        List<PayOrderEntity> orders = payOrderService.searchByPage(condition).getData();
        return orders.isEmpty() ? null : orders.get(0);
    }

    private PayOrderEntity findPayOrderByBizOrder(String bizType, String bizOrderNo) {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setBizType(bizType);
        condition.setBizOrderNo(bizOrderNo);
        List<PayOrderEntity> orders = payOrderService.searchByPage(condition).getData();
        return orders.isEmpty() ? null : orders.get(0);
    }
}
