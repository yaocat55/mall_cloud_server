package cn.net.mall.auth.dto;

import cn.net.mall.annotation.ValidPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户手机号登录实体
 *
 * @date 2024/11/8 下午3:30
 */
@Schema(description = "手机号登录请求参数")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPhoneLoginDTO {

    /**
     * 手机号
     */
    @ValidPhone
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 短信验证码
     */
    @Schema(description = "短信验证码", example = "-")
    private String smsCode;
}
