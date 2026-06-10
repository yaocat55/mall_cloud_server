package cn.net.mall.basic.util;

import cn.net.mall.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @date 2024/10/4 下午4:22
 */
@Slf4j
public abstract class PinyinUtil {

    private PinyinUtil() {

    }

    /**
     * 汉字转拼音
     *
     * @param chinese 汉语
     * @return 汉语对应的拼音
     */
    public static String toPinyin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (String.valueOf(ch).matches("[\u4e00-\u9fa5]")) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    sb.append(pinyinArray[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    log.info("转换拼音失败，原因：", e);
                    throw new BusinessException("转换拼音失败");
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
