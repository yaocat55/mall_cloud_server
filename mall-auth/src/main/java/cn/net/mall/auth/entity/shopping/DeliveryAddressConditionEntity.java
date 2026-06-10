package cn.net.mall.auth.entity.shopping;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;

/**
 * 收货地址查询条件实体
 *
 * @date 2024-09-01 10:02:01
 */
@Data
public class DeliveryAddressConditionEntity extends RequestConditionEntity {

    /**
     * ID集合
     */
    private List<Long> idList;

    /**
     * ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 收货人姓名
     */
    private String receiverName;
    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 省份ID
     */
    private Long provinceId;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 区县ID
     */
    private Long districtId;

    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区县
     */
    private String district;
    /**
     * 邮编
     */
    private String postCode;
    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 是否默认地址 1：是 0：否
     */
    private Integer isDefault;
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
}
