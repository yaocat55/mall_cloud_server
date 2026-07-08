package cn.net.mall.sensitive;

/**
 * 敏感数据脱敏接口。
 *
 * <p>实现此接口可自定义脱敏策略，框架自动注册的默认实现 {@link CustomMaskService}
 * 使用 Hutool 将所有字符替换为星号（*），保留第一位字符可见。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * public class MyMaskService implements ICustomMaskService {
 *     public String maskData(String data) {
 *         return data.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
 *     }
 * }
 * }</pre>
 *
 * @date 2024/5/23 下午5:24
 */
public interface ICustomMaskService {
    /**
     * 对给定的数据进行脱敏处理。
     *
     * @param data 待脱敏的原始数据
     * @return 脱敏后的数据
     */
    String maskData(String data);
}
