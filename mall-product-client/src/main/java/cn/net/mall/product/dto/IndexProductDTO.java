package cn.net.mall.product.dto;

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
@Schema(name = "首页商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndexProductDTO extends BaseEntity {


    /**
     * 商品ID
     */
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 商品名称
     */
    @Schema(name = "商品名称")
    private String productName;

    /**
     * 规格
     */
    @Schema(name = "规格")
    private String model;

    /**
     * 价格
     */
    @Schema(name = "价格")
    private BigDecimal price;

    /**
     * 封面
     */
    @Schema(name = "封面")
    private String cover;

    /**
     * 排序
     */
    @Schema(name = "排序")
    private Integer sort;

    /**
     * 商品类型 1: 热门商品 2: 最新商品 3：秒杀商品
     */
    @Schema(name = "商品类型 1: 热门商品 2: 最新商品 3：秒杀商品")
    private Integer type;
}
