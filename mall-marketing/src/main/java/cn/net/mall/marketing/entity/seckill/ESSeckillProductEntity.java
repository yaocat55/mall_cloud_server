package cn.net.mall.marketing.entity.seckill;

import cn.net.mall.entity.EsBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Schema(name = "秒杀商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ESSeckillProductEntity extends EsBaseEntity {

    @Schema(name = "分类名称")
    private String categoryName;

    @Schema(name = "单位名称")
    private String unitName;

    @Schema(name = "品牌名称")
    private String brandName;

    @Schema(name = "商品名称")
    private String name;

    @Schema(name = "规格")
    private String model;

    @Schema(name = "原价")
    private BigDecimal costPrice;

    @Schema(name = "商品ID")
    private Long productId;

    @Schema(name = "预扣库存")
    private Integer withHoldQuantity;

    @Schema(name = "实际剩余库存")
    private Integer remainQuantity;

    @Schema(name = "秒杀价格")
    private BigDecimal price;

    @Schema(name = "秒杀开始时间")
    private Date startTime;

    @Schema(name = "秒杀结束时间")
    private Date endTime;

    @Schema(name = "封面图片")
    private String cover;
}
