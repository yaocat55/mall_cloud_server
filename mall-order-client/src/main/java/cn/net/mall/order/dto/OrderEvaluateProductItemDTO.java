package cn.net.mall.order.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单评价商品项")

public class OrderEvaluateProductItemDTO {
    private String productId;
    private Integer rating;
    @ValidSensitiveWordField
    private String content;
    private List<String> images;
    private Boolean isAnonymous;
}
