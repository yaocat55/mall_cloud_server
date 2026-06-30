package cn.net.mall.basic.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件对象
 *
 * @date 2024/5/5 下午5:21
 */
@Data
@Schema(description = "文件信息")

public class FileDTO {

    /**
     * 文件名称
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 下载地址
     */
    @Schema(description = "下载地址")
    private String downloadUrl;
}
