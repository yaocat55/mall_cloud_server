package cn.net.mall.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码实体
 *
 * @date 2024/1/15 下午4:29
 */
@Schema(description = "图形验证码 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaptchaDTO {
    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 验证码图片
     */
    private String img;
}
