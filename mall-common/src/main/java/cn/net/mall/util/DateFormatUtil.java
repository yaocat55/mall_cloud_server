package cn.net.mall.util;

import cn.net.mall.exception.BusinessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 时间格式工具
 *
 * @date 2024/1/24 上午11:47
 */
public abstract class DateFormatUtil {

    private static final Logger log = Logger.getLogger(DateFormatUtil.class.getName());

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    private static final int BAD_REQUEST_CODE = 400;

    private DateFormatUtil() {

    }

    /**
     * 获取文件名中用到的当前时间字符串
     *
     * @return 当前时间字符串
     */
    public static String nowForFile() {
        return parseToString(new Date(), YYYYMMDDHHMMSS);
    }

    /**
     * 获取当前时间字符串
     *
     * @return 当前时间字符串
     */
    public static String nowDay() {
        return parseToString(new Date(), YYYY_MM_DD);
    }

    /**
     * 获取当前时间字符串
     *
     * @return 当前时间字符串
     */
    public static String now() {
        return parseToString(new Date());
    }

    /**
     * 将时间转换成字符串格式
     *
     * @param date 指定时间
     * @return 字符串格式的时间
     */
    public static String parseToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return simpleDateFormat.format(date);
    }

    /**
     * 将时间转换成字符串格式
     *
     * @param date   指定时间
     * @param format 指定格式
     * @return 字符串格式的时间
     */
    public static String parseToString(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 将字符串格式转换成Date
     *
     * @param time 指定时间
     * @return Date
     */
    public static Date parseToDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            log.log(Level.WARNING, "时间转换错误", e);
            throw new BusinessException(BAD_REQUEST_CODE, "时间转换错误");
        }
    }
}
