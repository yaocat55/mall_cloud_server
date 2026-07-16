package cn.net.mall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "仪表盘统计数据")
public class DashboardDTO {

    @Schema(description = "订单总数")
    private int orderCount;
    @Schema(description = "今日订单数")
    private int orderTodayCount;
    @Schema(description = "商品总数")
    private int productCount;
    @Schema(description = "用户总数")
    private int userCount;
    @Schema(description = "今日注册用户数")
    private int userTodayCount;
    @Schema(description = "退款总数")
    private int refundCount;
    @Schema(description = "待审核退款数")
    private int refundPendingCount;
    @Schema(description = "总销售额")
    private BigDecimal totalSalesAmount;
    @Schema(description = "今日销售额")
    private BigDecimal todaySalesAmount;
    @Schema(description = "最近订单")
    private List<RecentOrderDTO> recentOrders;
    @Schema(description = "各状态订单统计")
    private List<OrderStatusCountDTO> orderByStatus;
    @Schema(description = "销量排行 Top 5")
    private List<TopProductDTO> topProducts;

    @Data
    @Schema(description = "最近订单")
    public static class RecentOrderDTO {
        @Schema(description = "订单ID")
        private Long id;
        @Schema(description = "订单编码")
        private String code;
        @Schema(description = "用户名称")
        private String userName;
        @Schema(description = "订单金额")
        private BigDecimal totalAmount;
        @Schema(description = "订单状态 1=待付款 2=已支付 3=已发货 4=已完成")
        private int orderStatus;
        @Schema(description = "下单时间")
        private String orderTime;
    }

    @Data
    @Schema(description = "订单状态统计")
    public static class OrderStatusCountDTO {
        @Schema(description = "状态值")
        private int status;
        @Schema(description = "状态名称")
        private String name;
        @Schema(description = "数量")
        private int count;
    }

    @Data
    @Schema(description = "销量排行")
    public static class TopProductDTO {
        @Schema(description = "商品ID")
        private Long id;
        @Schema(description = "商品名称")
        private String name;
        @Schema(description = "销量")
        private int saleCount;
        @Schema(description = "销售额")
        private BigDecimal totalAmount;
    }
}
