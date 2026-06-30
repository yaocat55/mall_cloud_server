package cn.net.mall.product.entity.web;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品评价实体
 *
 * @date 2024/9/6 下午12:23
 */
@Data
@Schema(description = "商品评论")

public class ProductCommentWebEntity {

    /**
     * 系统ID
     */
    @Schema(description = "系统ID")
    private Long id;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 父评论ID
     */
    @Schema(description = "上级ID")
    private Long parentId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 个人头像
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 用户昵称
     */
    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "用户名")
    private String userName;

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
     * 评价类型 1：好评 2：中评 3：差评
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 评价时间
     */
    @Schema(description = "create Time Str")
    private String createTimeStr;

    @Schema(description = "photos")
    private java.util.List<String> photos;
}
