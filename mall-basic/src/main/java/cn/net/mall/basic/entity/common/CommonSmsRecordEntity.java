package cn.net.mall.basic.entity.common;

import cn.net.mall.annotation.Sensitive;
import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 短信发送记录实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-11-08 13:03:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "短信发送记录")

public class CommonSmsRecordEntity extends BaseEntity {

    /**
     * 手机号
     */
    @Sensitive
    private String phone;

    /**
     * 验证码
     */
    private String smsCode;

    /**
     * 有效期
     */
    private Integer expireSecond;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 短信类型
     */
    private Integer type;
}
