package cn.net.mall.customer.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerEntity extends BaseEntity {
    private String phone;
    private String password;
    private String nickName;
    private Long avatarId;
    private String avatarUrl;
    private Integer sex;
    private String birthday;
    private String email;
    private String realName;
    private String idCard;
    private Long levelId;
    private String levelName;
    private Integer growth;
    private Integer points;
    private BigDecimal totalConsumption;
    private String registerSource;
    private String registerIp;
    private Integer loginCount;
    private Boolean validStatus;
    private String lastLoginIp;
    private String lastLoginCity;
    private Date lastLoginTime;
}
