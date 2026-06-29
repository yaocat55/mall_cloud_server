package cn.net.mall.marketing.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 优惠券发放查询条件实体
 *
 * @date 2024-09-13 15:38:33
 */
@Data
public class CouponUserProvideConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    private List<Long> idList;

    /**
     * ID
     */
    private Long id;
    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券ID集合
     */
    private List<Long> couponIdList;

    /**
     * 商品ID，0表示所有商品
     */
    private Long productId;


    /**
     * 用户ID，0表示所有用户
     */
    private Long userId;
    /**
     * 有效状态 1:有效 0:无效
     */
    private Integer validStatus;
    /**
     * 创建人ID
     */
    private Long createUserId;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 修改人ID
     */
    private Long updateUserId;
    /**
     * 修改人名称
     */
    private String updateUserName;
    /**
     * 是否删除 1：已删除 0：未删除
     */
    private Integer isDel;

    /**
     * 已领取数量
     */
    @Schema(description = "已领取数量", example = "0")
    private Integer receiveCount;
}
