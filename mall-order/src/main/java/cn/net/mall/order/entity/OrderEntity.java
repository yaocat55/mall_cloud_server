package cn.net.mall.order.entity;

import cn.net.mall.annotation.MaxMoney;
import cn.net.mall.annotation.MinMoney;
import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体
 */
@Schema(description = "订单实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderEntity extends BaseEntity {

    /**
     * 订单编码
     */
    @NotEmpty(message = "订单编码不能为空")
    @Schema(description = "订单编码", example = "CODE_001")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 订单编码hash
     */
    @Schema(description = "订单编码hash", example = "0")
    private Integer codeHash;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称", example = "admin")
    private String userName;

    /**
     * 下单时间
     */
    @Schema(description = "下单时间", example = "2024-01-01")
    private Date orderTime;

    /**
     * 订单状态 1:已下单 2:已支付 3：已发货 4：已完成 5：已取消 6：已退货 7：已评价
     */
    @Schema(description = "订单状态 1:已下单 2:已支付 3：已发货 4：已完成 5：已取消 6：已退货 7：已评价", example = "1")
    private Integer orderStatus;

    /**
     * 支付状态 1:待支付 2:已支付 3：退款
     */
    @Schema(description = "支付状态 1:待支付 2:已支付 3：退款", example = "1")
    private Integer payStatus;

    /**
     * 总金额
     */
@NotNull(message = "总金额不能为空")
@Schema(description = "总金额", example = "99.99")
@MinMoney(value = 0, message = "总金额不能小于0")
@MaxMoney(value = 100000, message = "总金额必须小于100000")
    private BigDecimal totalAmount;

    /**
     * 付款金额
     */
@NotNull(message = "付款金额不能为空")
@Schema(description = "付款金额", example = "99.99")
@MinMoney(value = 0, message = "付款金额不能小于0")
@MaxMoney(value = 100000, message = "付款金额必须小于100000")
    private BigDecimal paymentAmount;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注")
    private String remark;

    /**
     * 订单明细列表
     */
    @Schema(description = "订单明细列表")
    private List<OrderItemEntity> orderItemList;

    /**
     * 订单收货地址
     */
    @Schema(description = "订单收货地址")
    private OrderDeliveryAddressEntity orderDeliveryAddress;
}
