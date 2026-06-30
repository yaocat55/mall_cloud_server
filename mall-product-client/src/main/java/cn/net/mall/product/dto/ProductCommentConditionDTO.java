package cn.net.mall.product.dto;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品评论查询条件实体
 *
 * @date 2024-08-31 15:50:38
 */
@Data
@Schema(description = "商品评论")

public class ProductCommentConditionDTO extends RequestConditionEntity {

    /**
     * ID集合
     */
    @Schema(description = "id List")
    private List<Long> idList;

    /**
     * ID
     */
    @Schema(description = "系统ID")
    private Long id;
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
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long orderId;

    /**
     * 商品ID集合
     */
    @Schema(description = "product Id List")
    private List<Long> productIdList;
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
    @Schema(description = "评分")
    private Integer rating;
    /**
     * 创建人ID
     */
    @Schema(description = "create User Id")
    private Long createUserId;
    /**
     * 创建人名称
     */
    @Schema(description = "create User Name")
    private String createUserName;
    /**
     * 修改人ID
     */
    @Schema(description = "update User Id")
    private Long updateUserId;
    /**
     * 修改人名称
     */
    @Schema(description = "update User Name")
    private String updateUserName;
    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(description = "是否删除 0:未删除 1:已删除")
    private Integer isDel;

    /**
     * 评论类型
     */
    @Schema(description = "类型")
    private Integer type;
}
