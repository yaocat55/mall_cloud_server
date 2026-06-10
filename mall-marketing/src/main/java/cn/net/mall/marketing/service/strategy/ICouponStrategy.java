package cn.net.mall.marketing.service.strategy;


import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.enums.CouponTypeEnum;

import java.math.BigDecimal;

/**
 * 使用优惠券计算支付金额接口
 *
 * @date 2024/9/18 下午3:22
 */
public interface ICouponStrategy {

    /**
     * 获取优惠券类型
     *
     * @return
     */
    CouponTypeEnum getType();

    /**
     * 使用优惠券后计算支付金额
     *
     * @param money        原始金额
     * @param couponEntity 优惠券实体
     * @return 优惠后的支付金额
     */
    BigDecimal calcPayMoney(BigDecimal money, CouponWebEntity couponEntity);
}
