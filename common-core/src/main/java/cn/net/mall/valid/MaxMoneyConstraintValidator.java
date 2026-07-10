package cn.net.mall.valid;

import cn.net.mall.annotation.MaxMoney;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * 最大金额校验
 *
 * @date 2024/6/11 下午4:13
 */
public class MaxMoneyConstraintValidator implements ConstraintValidator<MaxMoney, BigDecimal> {

    private MaxMoney constraint;

    @Override
    public void initialize(MaxMoney constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && value.doubleValue() < constraint.value();
    }

}
