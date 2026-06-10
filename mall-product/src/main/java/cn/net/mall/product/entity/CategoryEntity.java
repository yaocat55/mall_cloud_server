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
@Schema(name = "分类实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntity extends BaseEntity {


    /**
     * 父分类ID
     */
    @NotNull(message = "父分类ID不能为空")
    @Schema(name = "父分类ID")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotEmpty(message = "分类名称不能为空")
    @Schema(name = "分类名称")
    private String name;

    /**
     * 层级
     */
    @Schema(name = "层级")
    private Integer level;

    /**
     * 是否叶子节点
     */
    @Schema(name = "是否叶子节点")
    private Integer isLeaf;

    /**
     * 图片地址
     */
    @Schema(name = "图片地址")
    private String pic;

    /**
     * 背景颜色
     */
    @Schema(name = "背景颜色")
    private String bgColor;

    /**
     * 图标
     */
    @Schema(name = "图标")
    private String iconUrl;
}
