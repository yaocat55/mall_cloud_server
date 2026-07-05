package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户头像实体
 *
 * @date 2025/5/27 17:20
 */
@Schema(description = "用户头像实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAvatarDTO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 文件名称
     */
    @NotEmpty(message = "文件名称不能为空")
    @Schema(description = "文件名称", example = "-")
    private String fileName;

    /**
     * 文件地址
     */
    @NotEmpty(message = "文件地址不能为空")
    @Schema(description = "文件地址", example = "-")
    private String fileUrl;
}