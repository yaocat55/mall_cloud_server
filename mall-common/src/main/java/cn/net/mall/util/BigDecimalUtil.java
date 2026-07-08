package cn.net.mall.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal 工具类
 *
 * @date 2024/9/6 下午4:38
 */
public abstract class BigDecimalUtil {

    private BigDecimalUtil() {

    }

    /**
     * 四舍五入
     *
     * @param value 值
     * @param scale 保留小数位数
     * @return 四舍五入后的值
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入的字符串
     *
     * @param value 值
     * @param scale 保留小数位数
     * @return 四舍五入后的字符串
     */
    public static String roundToString(BigDecimal value, int scale) {
        return String.valueOf(value.setScale(scale, RoundingMode.HALF_UP));
    }
}
