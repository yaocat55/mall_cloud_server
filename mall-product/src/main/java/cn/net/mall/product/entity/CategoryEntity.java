package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 分类实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(description = "分类实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntity extends BaseEntity {


    /**
     * 父分类ID
     */
    @NotNull(message = "父分类ID不能为空")
    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotEmpty(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "测试数据")
    private String name;

    /**
     * 层级
     */
    @Schema(description = "层级", example = "1")
    private Integer level;

    /**
     * 是否叶子节点
     */
    @Schema(description = "是否叶子节点", example = "0")
    private Integer isLeaf;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址", example = "-")
    private String pic;

    /**
     * 背景颜色
     */
    @Schema(description = "背景颜色", example = "-")
    private String bgColor;

    /**
     * 图标
     */
    @Schema(description = "图标", example = "-")
    private String iconUrl;
}
