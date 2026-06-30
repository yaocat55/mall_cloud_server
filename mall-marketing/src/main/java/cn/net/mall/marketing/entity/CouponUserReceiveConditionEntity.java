package cn.net.mall.marketing.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券领取查询条件实体
 *
 * @date 2024-09-13 15:38:33
 */
@Data
@Schema(description = "CouponUserReceive信息")

public class CouponUserReceiveConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

    /**
     * ID
     */
    @Schema(description = "系统ID")
    private Long id;
    /**
     * 优惠券ID
     */
    @Schema(description = "优惠券ID")
    private Long couponId;

    /**
     * 优惠券ID集合
     */
    @Schema(description = "优惠券ID列表")
    private List<Long> couponIdList;

    /**
     * 用户ID，0表示所有用户
     */
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 使用状态 1:已使用 0:未使用
     */
    @Schema(description = "use Status")
    private Integer useStatus;
    /**
     * 使用时间
     */
    @Schema(description = "use Time")
    private Date useTime;
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
    @Schema(description = "是否删除")
    private Integer isDel;
}
