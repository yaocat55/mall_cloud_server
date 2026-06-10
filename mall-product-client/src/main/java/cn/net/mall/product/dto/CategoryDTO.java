package cn.net.mall.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类Web实体
 *
 * @date 2024-05-09 14:43:55
 */
@Schema(name = "分类实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {

    /**
     * 分类ID
     */
    @Schema(name = "分类ID")
    private Long id;

    /**
     * 父分类ID
     */
    @Schema(name = "父分类ID")
    private Long parentId;

    /**
     * 分类名称
     */
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
