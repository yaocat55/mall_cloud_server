package cn.net.mall.sensitive;

/**
 * 处理脱敏数据的接口
 *
 * @date 2024/5/23 下午5:24
 */
public interface ICustomMaskService {
    /**
     * 脱敏方法
     *
     * @param data 数据
     * @return 脱敏后的数据
     */
    String maskData(String data);
}
