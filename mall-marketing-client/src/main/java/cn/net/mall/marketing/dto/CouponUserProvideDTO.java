package cn.net.mall.marketing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "发券记录请求参数")
public class CouponUserProvideDTO {

    @Schema(description = "记录ID（修改时必传）", example = "1")
    private Long id;

    @Schema(description = "优惠券ID", example = "1")
    private Long couponId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "发放数量", example = "1")
    private Integer provideCount;
}
