package cn.net.mall.marketing.service.strategy;

import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.enums.CouponTypeEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static cn.net.mall.constant.NumberConstant.NUMBER_100;

/**
 * 满N件折扣
 *
 * @date 2024/9/18 下午3:25
 */
@Component
public class FullCountDiscountStrategy implements ICouponStrategy {
    @Override
    public CouponTypeEnum getType() {
        return CouponTypeEnum.FULL_COUNT_DISCOUNT;
    }

    @Override
    public BigDecimal calcPayMoney(BigDecimal money, CouponWebEntity couponEntity) {
        return money.multiply(new BigDecimal(couponEntity.getDiscount())).divide(new BigDecimal(NUMBER_100));
    }
}
