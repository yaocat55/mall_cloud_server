package cn.net.mall.order.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 订单收货地址查询条件实体
 */
@Data
@Schema(description = "订单配送地址")

public class OrderDeliveryAddressConditionEntity extends RequestConditionEntity {

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
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 订单编码
     */
    @Schema(description = "order Code")
    private String orderCode;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 用户名称
     */
    @Schema(description = "用户名")
    private String userName;
    /**
     * 收货人姓名
     */
    @Schema(description = "收货人姓名")
    private String receiverName;
    /**
     * 收货人手机号
     */
    @Schema(description = "收货人手机号")
    private String receiverPhone;
    /**
     * 省份
     */
    @Schema(description = "省份")
    private String province;
    /**
     * 城市
     */
    @Schema(description = "城市")
    private String city;
    /**
     * 区县
     */
    @Schema(description = "区县")
    private String district;
    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String detailAddress;
    /**
     * 邮编
     */
    @Schema(description = "邮编")
    private String postCode;
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
