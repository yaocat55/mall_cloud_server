package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "领取优惠券请求参数")
public class CouponReceiveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券发放ID")
    private Long id;
}
