package cn.net.mall.valid;

import cn.net.mall.annotation.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 手机号校验
 *
 * @date 2024/9/24 下午3:12
 */
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    private static final String PHONE_REGEX = "^1([38][0-9]|4[5-9]|5[0-3,5-9]|6[6]|7[0-8]|9[89])[0-9]{8}$";
    private final Pattern pattern = Pattern.compile(PHONE_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}
