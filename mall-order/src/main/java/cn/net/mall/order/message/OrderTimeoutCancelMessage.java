package cn.net.mall.order.message;

import lombok.Data;

@Data
public class OrderTimeoutCancelMessage {
    private Long orderId;
    private String code;
}
