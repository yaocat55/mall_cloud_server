package cn.net.mall.order.dto;

import cn.net.mall.product.dto.CouponDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "订单优惠券信息")
public class OrderCouponDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "可使用优惠券列表")
    private List<CouponDTO> canUseCouponList;

    @Schema(description = "不可使用优惠券列表")
    private List<CouponDTO> unCanUseCouponList;

    @Schema(description = "优惠券总数量", example = "0")
    private int totalCount;
}
