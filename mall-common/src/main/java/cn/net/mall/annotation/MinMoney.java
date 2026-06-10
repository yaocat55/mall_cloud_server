package cn.net.mall.annotation;

/**
 * @date 2024/6/11 下午4:12
 */

import cn.net.mall.valid.MinMoneyConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 最小值约束.
 *
 * @date 2024/6/11 下午4:13
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinMoneyConstraintValidator.class)
public @interface MinMoney {
    /**
     * message.
     *
     * @return
     */
    String message() default "{minMoney.message.error}";

    /**
     * min value.
     *
     * @return
     */
    double value() default 0;

    /**
     * group.
     *
     * @return
     */
    Class<?>[] groups() default {};

    /**
     * payload.
     *
     * @return
     */
    Class<? extends Payload>[] payload() default {};
}
