package cn.net.mall.basic.entity.common.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 短信发送记录实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-11-08 13:03:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "短信发送记录")

public class CommonSmsRecordWebEntity {

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 验证码uuid
     */
    @NotEmpty(message = "验证码uuid不能为空")
    @Schema(description = "captcha Uuid")
    private String captchaUuid;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    @Schema(description = "captcha Code")
    private String captchaCode;

    /**
     * 短信类型
     */
    @NotNull(message = "短信类型不能为空")
    @Schema(description = "类型")
    private Integer type;

    /**
     * 有效期
     */
    @Schema(description = "expire Second")
    private Long expireSecond;


}
