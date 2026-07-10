package cn.net.mall.sensitive;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 默认的敏感数据脱敏实现。
 *
 * <p>基于 Hutool 的 {@link cn.hutool.core.text.CharSequenceUtil#hide(CharSequence, int, int)} 方法，
 * 将数据从第 1 位（含）之后的所有字符替换为星号（*），保留第 0 位字符可见。</p>
 *
 * <p>示例：{@code "13800001111"} 脱敏后为 {@code "1***********"}</p>
 *
 * @date 2024/5/23 下午6:04
 */
public class CustomMaskService implements ICustomMaskService {
    @Override
    public String maskData(String data) {
        return CharSequenceUtil.hide(data, 1, data.length());
    }
}
