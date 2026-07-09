package cn.net.mall.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * C端用户更新信息请求参数
 */
@Schema(description = "C端用户更新信息请求参数")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "昵称", example = "小明")
    private String nickName;

    @Schema(description = "性别 0:未知 1:男 2:女", example = "1")
    private Integer sex;

    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
}
