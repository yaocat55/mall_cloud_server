package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单操作请求参数
 */
@Data
@Schema(description = "订单操作请求参数")
public class OrderOperateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编码")
    private String tradeCode;
}
