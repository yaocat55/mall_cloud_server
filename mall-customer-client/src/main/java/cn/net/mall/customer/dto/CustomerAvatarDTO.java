package cn.net.mall.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * C端用户头像 DTO
 */
@Schema(description = "C端用户头像 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerAvatarDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotEmpty(message = "文件名称不能为空")
    @Schema(description = "文件名称", example = "avatar.png")
    private String fileName;

    @NotEmpty(message = "文件地址不能为空")
    @Schema(description = "文件地址", example = "https://example.com/avatar.png")
    private String fileUrl;
}
