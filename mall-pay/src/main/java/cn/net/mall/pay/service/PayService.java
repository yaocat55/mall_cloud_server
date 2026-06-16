package cn.net.mall.pay.service;

import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.order.enums.OrderStatusEnum;
import cn.net.mall.pay.enums.PayStatusEnum;
import cn.net.mall.order.client.OrderFeignClient;
import cn.net.mall.order.dto.OrderDTO;
import cn.net.mall.pay.dto.PayWebDTO;
import cn.net.mall.util.FillUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 支付服务
 */
@Slf4j
@Service
public class PayService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 模拟支付接口
     *
     * @param payWebDTO 参数
     * @return 是否支付成功
     */
    public Boolean mockPay(PayWebDTO payWebDTO) {
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        if (Objects.isNull(currentUserInfo)) {
            log.warn("模拟支付失败，当前用户未登录");
            return Boolean.FALSE;
        }

        if (payWebDTO == null || !StringUtils.hasText(payWebDTO.getTradeCode())) {
            log.warn("模拟支付失败，tradeCode为空");
            return Boolean.FALSE;
        }

        OrderDTO orderDTO = orderFeignClient.getTrade(payWebDTO.getTradeCode());
        if (Objects.isNull(orderDTO)) {
            log.warn("模拟支付失败，当前订单不存在");
            return Boolean.FALSE;
        }

        orderDTO.setOrderStatus(OrderStatusEnum.PAY.getValue());
        orderDTO.setPayStatus(PayStatusEnum.PAYMENT.getValue());
        
        FillUserUtil.fillUpdateUserInfo(orderDTO);
        
        orderFeignClient.update(orderDTO);
        return Boolean.TRUE;
    }
}
