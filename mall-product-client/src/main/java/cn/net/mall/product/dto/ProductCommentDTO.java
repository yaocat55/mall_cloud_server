package cn.net.mall.product.dto;

import cn.net.mall.annotation.ValidSensitiveWordField;
import lombok.Data;

import java.util.List;

/**
 * 商品评价实体
 *
 * @date 2024/9/6 下午12:23
 */
@Data
public class ProductCommentDTO {

    /**
     * 系统ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 个人头像
     */
    private String avatarUrl;

    /**
     * 用户昵称
     */
    private String nickName;

    private String userName;

    /**
     * 评论内容
     */
    @ValidSensitiveWordField
    private String content;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评价类型 1：好评 2：中评 3：差评
     */
    private Integer type;

    /**
     * 评价时间
     */
    private String createTimeStr;

    private List<String> photos;
}
