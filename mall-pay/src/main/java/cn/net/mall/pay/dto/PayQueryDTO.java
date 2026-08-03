package cn.net.mall.pay.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询支付状态入参.
 */
@Data
@Schema(description = "查询支付状态入参")
public class PayQueryDTO {

    /** 支付订单号（payOrderNo 或 bizOrderNo 二选一） */
    @Schema(description = "支付订单号")
    private String payOrderNo;

    /** 业务方订单号（payOrderNo 或 bizOrderNo 二选一） */
    @Schema(description = "业务方订单号")
    private String bizOrderNo;

    /** 业务类型（按 bizOrderNo 查询时必填） */
    @Schema(description = "业务类型")
    private String bizType;
}
