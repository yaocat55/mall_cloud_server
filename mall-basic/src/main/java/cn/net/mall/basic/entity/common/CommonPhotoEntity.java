package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-03 16:43:09
 */
@Schema(name = "图片实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonPhotoEntity extends BaseEntity {


    /**
     * 图片分组ID
     */
    @Schema(name = "图片分组ID")
    private Long photoGroupId;

    /**
     * 图片名称
     */
    @Schema(name = "图片名称")
    private String name;

    /**
     * 图片url
     */
    @Schema(name = "图片url")
    private String url;
}
