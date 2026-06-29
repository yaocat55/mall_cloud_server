package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "订单商品评价视图")
public class OrderProductCommentViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "-")
    private String productName;

    @Schema(description = "规格", example = "型号")
    private String model;

    @Schema(description = "价格", example = "99.99")
    private BigDecimal price;

    @Schema(description = "封面图片", example = "https://example.com/cover.png")
    private String coverUrl;

    @Schema(description = "评价内容", example = "-")
    private String commentContent;

    @Schema(description = "评分", example = "0")
    private Integer rating;

    @Schema(description = "是否已评价", example = "false")
    private Boolean hasComment;
}
