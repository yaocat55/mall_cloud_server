package cn.net.mall.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码实体
 *
 * @date 2024/1/15 下午4:29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaptchaEntity {
    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 验证码图片
     */
    private String img;
}
