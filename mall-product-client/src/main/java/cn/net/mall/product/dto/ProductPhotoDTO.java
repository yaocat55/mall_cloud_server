package cn.net.mall.product.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品图片实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-05-09 14:43:56
 */
@Schema(name = "商品图片实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductPhotoDTO extends BaseEntity {


    /**
     * 商品ID
     */
    @Schema(name = "商品ID")
    private Long productId;

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

    /**
     * 排序
     */
    @Schema(name = "排序")
    private Integer sort;

    /**
     * 图片类型 1：封面 2：轮播图
     */
    @Schema(name = "图片类型")
    private Integer type;
}
