package cn.net.mall.marketing.entity.seckill;

import cn.net.mall.entity.EsBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "秒杀商品实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ESSeckillProductEntity extends EsBaseEntity {

    @Schema(description = "分类名称", example = "-")
    private String categoryName;

    @Schema(description = "单位名称", example = "-")
    private String unitName;

    @Schema(description = "品牌名称", example = "-")
    private String brandName;

    @Schema(description = "商品名称", example = "测试数据")
    private String name;

    @Schema(description = "规格", example = "型号")
    private String model;

    @Schema(description = "原价", example = "99.99")
    private BigDecimal costPrice;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "预扣库存", example = "0")
    private Integer withHoldQuantity;

    @Schema(description = "实际剩余库存", example = "100")
    private Integer remainQuantity;

    @Schema(description = "秒杀价格", example = "99.99")
    private BigDecimal price;

    @Schema(description = "秒杀开始时间", example = "2024-01-01")
    private Date startTime;

    @Schema(description = "秒杀结束时间", example = "2024-01-01")
    private Date endTime;

    @Schema(description = "封面图片", example = "-")
    private String cover;
}
