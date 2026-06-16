package cn.net.mall.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    CREATE(1, "已下单"),
    PAY(2, "已支付"),
    SHIPPED(3, "已发货"),
    FINISH(4, "已完成"),
    CANCEL(5, "已取消"),
    COMMENT(7, "已评价"),
    RETURN_APPLY(8, "退货申请"),
    RETURN(9, "已退货");

    private Integer value;
    private String desc;
}
