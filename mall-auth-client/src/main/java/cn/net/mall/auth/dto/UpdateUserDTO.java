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
@Schema(name = "用户修改实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserDTO {


    /**
     * 系统ID
     */
    @NotNull(message = "系统ID不能为空")
    @Schema(name = "系统ID")
    private Long id;

    /**
     * 邮箱
     */
    @Schema(name = "邮箱")
    private String email;

    /**
     * 别名
     */
    @Schema(name = "别名")
    private String nickName;

    /**
     * 性别 1：男 2：女
     */
    @Schema(name = "性别 1：男 2：女")
    private Integer sex;

    /**
     * 生日
     */
    @Schema(name = "生日")
    private String birthday;
}


