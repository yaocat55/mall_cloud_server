package cn.net.mall.admin.entity.shopping;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 收货地址查询条件实体
 *
 * @date 2024-09-01 10:02:01
 */
@Data
@Schema(description = "收货地址")

public class DeliveryAddressConditionEntity extends RequestConditionEntity {

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
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
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
     * 省份ID
     */
    @Schema(description = "province Id")
    private Long provinceId;

    /**
     * 城市ID
     */
    @Schema(description = "city Id")
    private Long cityId;

    /**
     * 区县ID
     */
    @Schema(description = "district Id")
    private Long districtId;

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
     * 邮编
     */
    @Schema(description = "邮编")
    private String postCode;
    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String detailAddress;
    /**
     * 是否默认地址 1：是 0：否
     */
    @Schema(description = "是否默认")
    private Integer isDefault;
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
