package cn.net.mall.valid;

import cn.net.mall.annotation.MinMoney;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * 最小金额校验
 *
 * @date 2024/6/11 下午4:13
 */
public class MinMoneyConstraintValidator implements ConstraintValidator<MinMoney, BigDecimal> {

    private MinMoney constraint;

    @Override
    public void initialize(MinMoney constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && value.doubleValue() >= constraint.value();
    }

}
