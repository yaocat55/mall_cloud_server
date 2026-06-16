package cn.net.mall.pay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PayStatusEnum {

    WAIT_PAY(1, "待支付"),
    PAYMENT(2, "已支付"),
    REFUND(3, "已退款"),
    FAILURE(4, "失败");

    private Integer value;
    private String desc;
}
