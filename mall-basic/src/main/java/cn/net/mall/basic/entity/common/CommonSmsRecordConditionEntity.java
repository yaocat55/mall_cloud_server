package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 短信发送记录查询条件实体
 *
 * @date 2024-11-08 13:03:15
 */
@Data
@Schema(description = "短信发送记录")

public class CommonSmsRecordConditionEntity extends RequestConditionEntity {

   /**
    * ID集合
    */
    @Schema(description = "系统ID列表")
    private List<Long> idList;

	/**
	 *  ID
     */
	@Schema(description = "系统ID")
	private Long id;
	/**
	 *  手机号
     */
	@Schema(description = "手机号")
	private String phone;
	/**
	 *  验证码
     */
	@Schema(description = "sms Code")
	private String smsCode;
	/**
	 *  有效期
     */
	@Schema(description = "expire Second")
	private Integer expireSecond;
	/**
	 *  发送时间
     */
	@Schema(description = "send Time")
	private Date sendTime;
	/**
	 *  创建人ID
     */
	@Schema(description = "create User Id")
	private Long createUserId;
	/**
	 *  创建人名称
     */
	@Schema(description = "create User Name")
	private String createUserName;
	/**
	 *  修改人ID
     */
	@Schema(description = "update User Id")
	private Long updateUserId;
	/**
	 *  修改人名称
     */
	@Schema(description = "update User Name")
	private String updateUserName;
	/**
	 *  是否删除 1：已删除 0：未删除
     */
	@Schema(description = "是否删除")
	private Integer isDel;
}
