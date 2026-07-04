package cn.net.mall.basic.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片分组 DTO
 *
 * @date 2025-07-03
 */
@Schema(description = "图片分组DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonPhotoGroupDTO extends BaseEntity {

    @Schema(description = "分组名称", example = "测试数据")
    private String name;
}
