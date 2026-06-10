package cn.net.mall.util;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 随机数工具
 *
 * @date 2024/8/8 8:09
 */
public abstract class RandomUtil {

    private static final Random random = new Random();
    private static final DecimalFormat FOUR_DF = new DecimalFormat("0000");
    private static final DecimalFormat SIX_DF = new DecimalFormat("000000");

    private RandomUtil() {

    }

    public static String getFourBitRandom() {
        return FOUR_DF.format(random.nextInt(10000));
    }

    public static String getSixBitRandom() {
        return SIX_DF.format(random.nextInt(1000000));
    }
}
