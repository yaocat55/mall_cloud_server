package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 首页商品实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-08-27 17:37:52
 */
@Schema(description = "首页商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndexProductEntity extends BaseEntity {


    /**
     * 商品ID
     */
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "-")
    private String productName;

    /**
     * 规格
     */
    @Schema(description = "规格", example = "型号")
    private String model;

    /**
     * 价格
     */
    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

    /**
     * 封面
     */
    @Schema(description = "封面", example = "-")
    private String cover;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 商品类型 1: 热门商品 2: 最新商品 3：秒杀商品
     */
    @Schema(description = "商品类型 1: 热门商品 2: 最新商品 3：秒杀商品", example = "1")
    private Integer type;
}
