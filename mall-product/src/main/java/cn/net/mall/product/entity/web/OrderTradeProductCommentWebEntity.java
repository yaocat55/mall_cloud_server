package cn.net.mall.product.entity.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单商品评价实体
 *
 * @date 2024/10/25 上午10:11
 */
@Schema(description = "订单商品评价实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderTradeProductCommentWebEntity {

    /**
     * 订单code
     */
    @NotEmpty(message = "订单Code不能为空")
    private String tradeCode;

    /**
     * 评价集合
     */
    @NotEmpty(message = "评价集合不能为空")
    private List<ProductCommentWebEntity> productCommentWebEntityList;
}
