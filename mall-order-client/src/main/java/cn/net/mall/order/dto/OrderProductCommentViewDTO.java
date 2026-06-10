package cn.net.mall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "订单商品评价视图")
public class OrderProductCommentViewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "规格")
    private String model;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "封面图片")
    private String coverUrl;

    @Schema(description = "评价内容")
    private String commentContent;

    @Schema(description = "评分")
    private Integer rating;

    @Schema(description = "是否已评价")
    private Boolean hasComment;
}
