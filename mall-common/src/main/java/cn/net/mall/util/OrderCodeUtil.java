package cn.net.mall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 订单编号生成工具
 *
 * @date 2024/5/30 下午6:02
 */
public class OrderCodeUtil {

    public static final int ORDER_CODE_LENGTH = 24;
    private static final String ORDER_CODE_DATA_FORMAT = "yyyyMMddHHmmss";
    private static final int ORDER_CODE_RANDOM_NUMBER = 10;
    private static final int ORDER_CODE_RANDOM_LENGTH = 6;


    /**
     * 生成订单编号
     *
     * @return 订单编号
     */
    public static String generateOrderCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ORDER_CODE_DATA_FORMAT);
        String random = getRandom(ORDER_CODE_RANDOM_LENGTH);
        Date date = new Date();
        String time = dateFormat.format(date);
        String code = "XS" + time + random;
        while (code.length() < ORDER_CODE_LENGTH) {
            code = code + 0;
        }
        return code;
    }

    private static String getRandom(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(ORDER_CODE_RANDOM_NUMBER));
        }
        return rs.toString();
    }
}
