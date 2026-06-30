package cn.net.mall.product.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品评价实体
 *
 * @date 2024/9/6 下午12:23
 */
@Data
@Schema(description = "商品评论")

public class ProductCommentDTO {

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
    private String avatarUrl;

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
    @ValidSensitiveWordField
    @Schema(description = "内容")
    private String content;

    /**
     * 评分
     */
    @Schema(description = "评分")
    private Integer rating;

    /**
     * 评价类型 1：好评 2：中评 3：差评
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 评价时间
     */
    @Schema(description = "创建时间(字符串)")
    private String createTimeStr;

    @Schema(description = "photos")
    private List<String> photos;
}
