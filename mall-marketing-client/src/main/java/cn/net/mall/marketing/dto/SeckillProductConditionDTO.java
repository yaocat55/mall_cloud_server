package cn.net.mall.marketing.dto;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 秒杀商品查询条件 DTO
 *
 * @date 2026-07-15
 */
@Data
@Schema(description = "秒杀商品查询条件")
public class SeckillProductConditionDTO extends RequestConditionEntity {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "ID集合")
    private List<Long> idList;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "预扣库存")
    private Integer withHoldQuantity;

    @Schema(description = "实际剩余库存")
    private Integer remainQuantity;

    @Schema(description = "秒杀价格")
    private BigDecimal price;

    @Schema(description = "秒杀开始时间")
    private Date startTime;

    @Schema(description = "秒杀结束时间")
    private Date endTime;
}
