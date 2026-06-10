package cn.net.mall.sensitive;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.stereotype.Service;

/**
 * @date 2024/5/23 下午6:04
 */
@Service
public class CustomMaskService implements ICustomMaskService {
    @Override
    public String maskData(String data) {
        return CharSequenceUtil.hide(data, 1, data.length());
    }
}
