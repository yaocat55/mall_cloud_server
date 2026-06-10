package cn.net.mall.marketing.service.strategy;

import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.enums.CouponTypeEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 阶梯满减
 *
 * @date 2024/9/18 下午3:25
 */
@Component
public class NormalFullReductionStrategy implements ICouponStrategy {
    @Override
    public CouponTypeEnum getType() {
        return CouponTypeEnum.NORMAL_FULL_REDUCTION;
    }

    @Override
    public BigDecimal calcPayMoney(BigDecimal money, CouponWebEntity couponEntity) {
        if (money.doubleValue() >= couponEntity.getMinMoney()) {
            return money.subtract(new BigDecimal(couponEntity.getOffMoney()));
        }
        return money;
    }
}
