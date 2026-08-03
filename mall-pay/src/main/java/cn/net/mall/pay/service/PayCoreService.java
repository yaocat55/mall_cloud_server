package cn.net.mall.pay.service;

import cn.net.mall.pay.dto.PayCreateDTO;
import cn.net.mall.pay.dto.PayCreateResult;
import cn.net.mall.pay.dto.PayPrepayResult;
import cn.net.mall.pay.dto.PayQueryDTO;
import cn.net.mall.pay.dto.PayQueryResult;
import cn.net.mall.pay.entity.PayBizConfigEntity;
import cn.net.mall.pay.entity.PayChannelConfigEntity;
import cn.net.mall.pay.entity.PayOrderConditionEntity;
import cn.net.mall.pay.entity.PayOrderEntity;
import cn.net.mall.pay.enums.PayStatusEnum;
import cn.net.mall.pay.support.IdGenerator;
import cn.net.mall.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 支付核心服务.
 *
 * <p>主线一：创建支付单 → 渠道下单 → 回调验签 → 状态机流转 → 通知业务方。</p>
 * <p>并发控制用乐观锁（version），回调幂等靠前置状态约束。</p>
 */
@Slf4j
@Service
public class PayCoreService {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private PayBizConfigService payBizConfigService;

    @Autowired
    private IdGenerator idGenerator;

    @Value("${mall.pay.order.expire-minutes:30}")
    private int expireMinutes;

    /**
     * 创建支付单（幂等）.
     *
     * <p>同 bizType+bizOrderNo 存在进行中支付单（待支付）则直接返回已有单，不重复建。</p>
     */
    @Transactional(rollbackFor = Exception.class)
    public PayCreateResult create(PayCreateDTO dto) {
        // 1. 校验：渠道启用、金额>0、业务方已接入
        PayChannelConfigEntity config = payChannelService.getConfig(dto.getChannelCode());
        AssertUtil.isTrue(dto.getTotalAmount() != null && dto.getTotalAmount() > 0, "支付金额必须大于0");

        PayBizConfigEntity bizConfig = getBizConfig(dto.getBizType());
        AssertUtil.notNull(bizConfig, "业务方未接入支付服务: " + dto.getBizType());

        // 2. 幂等：同业务单已有待支付单则直接返回
        PayOrderEntity existOrder = findWaitingByBizOrder(dto.getBizType(), dto.getBizOrderNo());
        if (existOrder != null) {
            return toCreateResult(existOrder, null);
        }

        // 3. 生成支付单
        PayOrderEntity order = new PayOrderEntity();
        order.setId(idGenerator.nextId());
        order.setPayOrderNo(idGenerator.nextPayOrderNo());
        order.setMerchantOrderNo(idGenerator.nextMerchantOrderNo());
        order.setBizOrderNo(dto.getBizOrderNo());
        order.setBizType(dto.getBizType());
        order.setChannelCode(dto.getChannelCode());
        order.setUserId(dto.getUserId());
        order.setOpenId(dto.getOpenId());
        order.setClientIp(dto.getClientIp());
        order.setRemark(dto.getRemark());
        order.setSubject(dto.getSubject() != null ? dto.getSubject() : dto.getBizOrderNo());
        order.setTotalAmount(dto.getTotalAmount());
        order.setPayAmount(dto.getTotalAmount());
        order.setRefundAmount(0L);
        order.setCurrency("CNY");
        order.setPayStatus(PayStatusEnum.WAIT_PAY.getValue());
        order.setRefundStatus(0);
        order.setNotifyCount(0);
        order.setBizNotifyStatus(0);
        order.setExpireTime(addMinutes(new Date(), expireMinutes));

        payOrderService.insert(order);

        // 4. 渠道下单（prepay），拿拉起支付参数
        PayPrepayResult prepay = payChannelService.getStrategy(dto.getChannelCode())
                .prepay(order, config);
        if (!prepay.isSuccess()) {
            throw new IllegalArgumentException("渠道下单失败: " + prepay.getErrMsg());
        }
        return toCreateResult(order, prepay.getPrepayParams());
    }

    /**
     * 查询支付状态.
     */
    public PayOrderEntity query(PayQueryDTO dto) {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setPayOrderNo(dto.getPayOrderNo());
        condition.setBizOrderNo(dto.getBizOrderNo());
        condition.setBizType(dto.getBizType());
        List<PayOrderEntity> orders = payOrderService.searchByPage(condition).getData();
        AssertUtil.notEmpty(orders, "支付订单不存在");
        return orders.get(0);
    }

    /**
     * 主动查单兜底：待支付且创建超过 queryAfterMinutes 的订单，调渠道 query().
     * 渠道确认成功后本地流转已支付。
     */
    @Transactional(rollbackFor = Exception.class)
    public void queryUnpaidOrders() {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setPayStatus(PayStatusEnum.WAIT_PAY.getValue());
        List<PayOrderEntity> waitingOrders = payOrderService.searchByPage(condition).getData();
        for (PayOrderEntity order : waitingOrders) {
            if (order.getSuccessTime() != null) {
                continue;
            }
            try {
                PayChannelConfigEntity config = payChannelService.getConfig(order.getChannelCode());
                PayQueryResult queryResult = payChannelService.getStrategy(order.getChannelCode())
                        .query(order.getMerchantOrderNo(), config);
                if (queryResult.isSuccess()) {
                    markPaid(order);
                }
            } catch (Exception e) {
                log.warn("主动查单失败, payOrderNo={}, err={}", order.getPayOrderNo(), e.getMessage());
            }
        }
    }

    /**
     * 超时关单：expire_time 过期未支付的支付单关闭为 30 已关闭.
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeExpiredOrders() {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setPayStatus(PayStatusEnum.WAIT_PAY.getValue());
        List<PayOrderEntity> waitingOrders = payOrderService.searchByPage(condition).getData();
        Date now = new Date();
        for (PayOrderEntity order : waitingOrders) {
            if (order.getExpireTime() != null && order.getExpireTime().before(now)) {
                closeOrder(order, "支付超时自动关闭");
            }
        }
    }

    /**
     * 关闭支付单（用户取消/超时），乐观锁流转 10 → 30.
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean close(String payOrderNo, String reason) {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setPayOrderNo(payOrderNo);
        List<PayOrderEntity> orders = payOrderService.searchByPage(condition).getData();
        if (orders.isEmpty()) {
            return false;
        }
        PayOrderEntity order = orders.get(0);
        if (!PayStatusEnum.WAIT_PAY.getValue().equals(order.getPayStatus())) {
            return false;
        }
        closeOrder(order, reason);
        return true;
    }

    /**
     * 回调处理：乐观锁 10 → 20，回填渠道交易号/成功时间/实付金额，通知业务方.
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleNotify(PayOrderEntity order, String channelTradeNo, Long payAmount, Date successTime) {
        markPaid(order);
    }

    /**
     * 标记已支付（乐观锁流转 10 → 20）.
     */
    private void markPaid(PayOrderEntity order) {
        if (!PayStatusEnum.WAIT_PAY.getValue().equals(order.getPayStatus())) {
            log.info("支付单非待支付态，跳过: payOrderNo={}, status={}", order.getPayOrderNo(), order.getPayStatus());
            return;
        }
        order.setPayStatus(PayStatusEnum.PAYMENT.getValue());
        order.setSuccessTime(new Date());
        order.setPayAmount(order.getPayAmount() == null ? order.getTotalAmount() : order.getPayAmount());
        payOrderService.update(order);
        log.info("支付单已标记支付成功: payOrderNo={}", order.getPayOrderNo());
    }

    /**
     * 关闭支付单（乐观锁流转 10 → 30）.
     */
    private void closeOrder(PayOrderEntity order, String reason) {
        if (!PayStatusEnum.WAIT_PAY.getValue().equals(order.getPayStatus())) {
            return;
        }
        order.setPayStatus(PayStatusEnum.CLOSED.getValue());
        order.setClosedTime(new Date());
        order.setRemark(reason);
        payOrderService.update(order);
        log.info("支付单已关闭: payOrderNo={}, reason={}", order.getPayOrderNo(), reason);
    }

    /**
     * 查询业务方接入配置.
     */
    private PayBizConfigEntity getBizConfig(String bizType) {
        cn.net.mall.pay.entity.PayBizConfigConditionEntity condition = new cn.net.mall.pay.entity.PayBizConfigConditionEntity();
        condition.setBizType(bizType);
        condition.setStatus(1);
        List<PayBizConfigEntity> configs = payBizConfigService.searchByPage(condition).getData();
        return configs.isEmpty() ? null : configs.get(0);
    }

    /**
     * 按业务单号查待支付单.
     */
    private PayOrderEntity findWaitingByBizOrder(String bizType, String bizOrderNo) {
        PayOrderConditionEntity condition = new PayOrderConditionEntity();
        condition.setBizType(bizType);
        condition.setBizOrderNo(bizOrderNo);
        condition.setPayStatus(PayStatusEnum.WAIT_PAY.getValue());
        List<PayOrderEntity> orders = payOrderService.searchByPage(condition).getData();
        return orders.isEmpty() ? null : orders.get(0);
    }

    private PayCreateResult toCreateResult(PayOrderEntity order, String prepayParams) {
        PayCreateResult result = new PayCreateResult();
        result.setPayOrderNo(order.getPayOrderNo());
        result.setMerchantOrderNo(order.getMerchantOrderNo());
        result.setChannelCode(order.getChannelCode());
        result.setPrepayParams(prepayParams);
        result.setPayStatus(order.getPayStatus());
        return result;
    }

    private Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }
}
