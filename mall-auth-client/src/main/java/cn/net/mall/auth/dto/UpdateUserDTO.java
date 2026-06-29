package cn.net.mall.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "更新用户信息请求参数")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserDTO {


    /**
     * 系统ID
     */
    @NotNull(message = "系统ID不能为空")
    @Schema(description = "系统ID", example = "1")
    private Long id;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "admin@mall.com")
    private String email;

    /**
     * 别名
     */
    @Schema(description = "昵称", example = "-")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(description = "性别：0-未知 1-男 2-女", example = "1")
    private Integer sex;

    /**
     * 生日
     */
    @Schema(description = "生日", example = "-")
    private String birthday;
}


