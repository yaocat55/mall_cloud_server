package cn.net.mall.annotation;

import cn.net.mall.enums.SensitiveTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据脱敏注解
 *
 * @date 2024/5/23 下午4:50
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    SensitiveTypeEnum type() default SensitiveTypeEnum.DEFAULT;
}
