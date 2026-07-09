package cn.net.mall.customer.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;

/**
 * 客户收货地址
 */
@Data
public class CustomerAddressEntity extends BaseEntity {
    private Long customerId;
    private String receiverName;
    private String receiverPhone;
    private Long provinceId;
    private String province;
    private Long cityId;
    private String city;
    private Long districtId;
    private String district;
    private String detailAddress;
    private String postCode;
    private Boolean isDefault;
    private String label;
}
