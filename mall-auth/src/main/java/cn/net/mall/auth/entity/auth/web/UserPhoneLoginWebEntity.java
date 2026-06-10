package cn.net.mall.auth.entity.auth.web;

import cn.net.mall.annotation.ValidPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 用户手机号登录web实体
 *
 * @date 2024/11/8 下午3:30
 */
@Schema(name = "用户手机号登录web实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPhoneLoginWebEntity {

    /**
     * 手机号
     */
    @ValidPhone
    @Schema(name = "手机号")
    private String phone;

    /**
     * 短信验证码
     */
    @Schema(name = "短信验证码")
    private String smsCode;
}
