package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品评论实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-08-31 15:50:38
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "商品评论")

public class ProductCommentEntity extends BaseEntity {

    @Schema(description = "用户名")
    private String userName;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long orderId;


    /**
     * 父评论ID
     */
    @Schema(description = "上级ID")
    private Long parentId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 评论内容
     */
    @Schema(description = "内容")
    private String content;

    /**
     * 评分
     */
    @Schema(description = "rating")
    private Integer rating;

    /**
     * 评论类型
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 图片地址
     */
    @Schema(description = "头像URL")
    private String avatarUrl;
}
