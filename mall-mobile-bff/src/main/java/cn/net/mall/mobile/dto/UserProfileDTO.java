package cn.net.mall.mobile.dto;

import cn.net.mall.customer.dto.CustomerUserDTO;
import cn.net.mall.order.dto.OrderTradeCountDTO;
import cn.net.mall.product.dto.IndexProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户中心聚合数据")
public class UserProfileDTO {

    @Schema(description = "用户基本信息（昵称、头像、手机号、会员信息等）")
    private CustomerUserDTO user;

    @Schema(description = "各状态订单数量统计")
    private OrderTradeCountDTO orderCounts;

    @Schema(description = "推荐商品列表（首页推荐商品，最多 4 个）")
    private List recommendProducts;
}
