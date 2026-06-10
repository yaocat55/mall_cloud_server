package cn.net.mall.marketing.service.strategy;

import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.enums.CouponTypeEnum;
import cn.net.mall.util.AssertUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @date 2024/9/18 下午3:27
 */
@Component
public class CouponContext {

    private static Map<CouponTypeEnum, ICouponStrategy> couponStrategyMap;
    private static final CouponContext COUPON_CONTEXT = new CouponContext();
    
    @Autowired
    private List<ICouponStrategy> strategyList;
    
    @PostConstruct
    public void init() {
        Map<CouponTypeEnum, ICouponStrategy> map = new java.util.EnumMap<>(CouponTypeEnum.class);
        if (strategyList != null) {
            for (ICouponStrategy s : strategyList) {
                map.put(s.getType(), s);
            }
        }
        initMap(map);
    }

    /**
     * 初始化优惠券实体map
     *
     * @param map 优惠券实体map
     */
    public void initMap(Map<CouponTypeEnum, ICouponStrategy> map) {
        if (MapUtils.isEmpty(couponStrategyMap)) {
            couponStrategyMap = map;
        }
    }


    public static CouponContext getInstance() {
        return COUPON_CONTEXT;
    }

    /**
     * 使用优惠券后计算支付金额
     *
     * @param money        原始金额
     * @param couponEntity 优惠券实体
     * @return 优惠后的支付金额
     */
    public BigDecimal calcPayMoney(BigDecimal money, CouponWebEntity couponEntity) {
        CouponTypeEnum couponTypeEnum = getCouponTypeEnum(couponEntity);
        if (Objects.isNull(couponTypeEnum)) {
            return money;
        }

        ICouponStrategy couponStrategy = couponStrategyMap.get(couponTypeEnum);
        AssertUtil.notNull(couponStrategy, "该优惠券类型不存在");
        return couponStrategy.calcPayMoney(money, couponEntity);
    }

    private CouponTypeEnum getCouponTypeEnum(CouponWebEntity couponEntity) {
        for (CouponTypeEnum couponTypeEnum : CouponTypeEnum.values()) {
            if (couponTypeEnum.getValue().equals(couponEntity.getType())) {
                return couponTypeEnum;
            }
        }
        return null;
    }
}
