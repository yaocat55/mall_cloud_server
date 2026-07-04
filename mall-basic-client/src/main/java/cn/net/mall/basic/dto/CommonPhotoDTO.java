package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "图片DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonPhotoDTO extends BaseEntity {

    @Schema(description = "图片分组ID", example = "0")
    private Long photoGroupId;

    @Schema(description = "图片名称", example = "测试数据")
    private String name;

    @Schema(description = "图片url", example = "https://example.com/image.png")
    private String url;
}
