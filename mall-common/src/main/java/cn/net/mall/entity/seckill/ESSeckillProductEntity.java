package cn.net.mall.entity.seckill;

import cn.net.mall.entity.EsBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-08 10:57:31
 */
@Schema(name = "秒杀商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ESSeckillProductEntity extends EsBaseEntity {

    /**
     * 分类名称
     */
    @Schema(name = "分类名称")
    private String categoryName;

    /**
     * 单位名称
     */
    @Schema(name = "单位名称")
    private String unitName;

    /**
     * 品牌名称
     */
    @Schema(name = "品牌名称")
    private String brandName;

    /**
     * 商品名称
     */
    @Schema(name = "商品名称")
    private String name;

    /**
     * 规格
     */
    @Schema(name = "规格")
    private String model;

    /**
     * 原价
     */
    @Schema(name = "原价")
    private BigDecimal costPrice;

    /**
     * 商品ID
     */
    @Schema(name = "商品ID")
    private Long productId;

    /**
     * 预扣库存
     */
    @Schema(name = "预扣库存")
    private Integer withHoldQuantity;

    /**
     * 实际剩余库存
     */
    @Schema(name = "实际剩余库存")
    private Integer remainQuantity;

    /**
     * 秒杀价格
     */
    @Schema(name = "秒杀价格")
    private BigDecimal price;

    /**
     * 秒杀开始时间
     */
    @Schema(name = "秒杀开始时间")
    private Date startTime;

    /**
     * 秒杀结束时间
     */
    @Schema(name = "秒杀结束时间")
    private Date endTime;

    /**
     * 封面图片
     */
    @Schema(name = "封面图片")
    private String cover;
}
