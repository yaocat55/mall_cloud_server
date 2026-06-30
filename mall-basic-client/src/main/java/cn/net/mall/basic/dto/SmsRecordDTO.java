package cn.net.mall.basic.dto;

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

public class SmsRecordDTO extends BaseEntity {

	/**
	 * 手机号
	 */
	@Sensitive
	@Schema(description = "手机号")
	private String phone;

	/**
	 * 验证码
	 */
	@Schema(description = "sms Code")
	private String smsCode;

	/**
	 * 有效期
	 */
	@Schema(description = "expire Second")
	private Integer expireSecond;

	/**
	 * 发送时间
	 */
	@Schema(description = "send Time")
	private Date sendTime;
}
