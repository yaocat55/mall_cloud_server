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
@Schema(description = "商品图片实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductPhotoDTO extends BaseEntity {


    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 图片名称
     */
    @Schema(description = "图片名称", example = "测试数据")
    private String name;

    /**
     * 图片url
     */
    @Schema(description = "图片url", example = "https://example.com/image.png")
    private String url;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 图片类型 1：封面 2：轮播图
     */
    @Schema(description = "图片类型", example = "1")
    private Integer type;
}
