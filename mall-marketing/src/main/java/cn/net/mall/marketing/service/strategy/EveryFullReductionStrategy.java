package cn.net.mall.marketing.service.strategy;

import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.enums.CouponTypeEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 每满减
 * <p>
 * 比如：每满100减30
 *
 * @date 2024/9/18 下午3:25
 */
@Component
public class EveryFullReductionStrategy implements ICouponStrategy {
    @Override
    public CouponTypeEnum getType() {
        return CouponTypeEnum.EVERY_FULL_REDUCTION;
    }

    @Override
    public BigDecimal calcPayMoney(BigDecimal money, CouponWebEntity couponEntity) {
        if (money.doubleValue() >= couponEntity.getMinMoney()) {
            BigDecimal value = money.divide(new BigDecimal(couponEntity.getMinMoney()));
            return money.subtract(new BigDecimal(value.intValue()).multiply(new BigDecimal(couponEntity.getOffMoney())));
        }
        return money;
    }
}
