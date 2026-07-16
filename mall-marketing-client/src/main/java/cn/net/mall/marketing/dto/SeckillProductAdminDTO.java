package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品管理操作 DTO（新增 / 修改）
 *
 * @date 2026-07-15
 */
@Data
@Schema(description = "秒杀商品管理操作")
public class SeckillProductAdminDTO {

    @Schema(description = "系统ID（修改时必传）")
    private Long id;

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
