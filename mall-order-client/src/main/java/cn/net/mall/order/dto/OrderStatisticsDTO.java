package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单统计数据")
public class OrderStatisticsDTO {

    @Schema(description = "订单总数")
    private int orderCount;

    @Schema(description = "今日订单数")
    private int orderTodayCount;

    @Schema(description = "总销售额")
    private BigDecimal totalSalesAmount = BigDecimal.ZERO;

    @Schema(description = "今日销售额")
    private BigDecimal todaySalesAmount = BigDecimal.ZERO;

    @Schema(description = "待付款")
    private int statusWaitPay;

    @Schema(description = "已支付/待发货")
    private int statusPaid;

    @Schema(description = "已发货")
    private int statusShipped;

    @Schema(description = "已完成")
    private int statusCompleted;
}
