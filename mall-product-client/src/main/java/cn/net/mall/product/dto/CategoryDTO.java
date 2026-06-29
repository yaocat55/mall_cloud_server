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
@Schema(description = "分类实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    /**
     * 分类名称
     */
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
