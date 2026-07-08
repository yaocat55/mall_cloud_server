package cn.net.mall.order.dto;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单收货地址传输对象", example = "0")
public class OrderDeliveryAddressDTO extends BaseEntity {
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    @Schema(description = "收货人名称", example = "-")
    private String receiverName;
    @Schema(description = "收货人电话", example = "-")
    private String receiverPhone;
    @Schema(description = "省份/直辖市", example = "-")
    private String receiverProvince;
    @Schema(description = "城市", example = "-")
    private String receiverCity;
    @Schema(description = "区", example = "-")
    private String receiverRegion;
    @Schema(description = "详细地址", example = "-")
    private String receiverDetailAddress;
}
