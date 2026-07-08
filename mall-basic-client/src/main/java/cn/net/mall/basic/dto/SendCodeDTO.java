package cn.net.mall.basic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 发送验证码请求DTO
 */
@Data
@Schema(description = "发送验证码请求")

public class SendCodeDTO {
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "string")
    private String phone;

    /**
     * 类型
     */
    @NotNull(message = "类型不能为空")
    @Schema(description = "类型", example = "0")
    private Integer type;
}