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
    private List<Long> idList;

	/**
	 *  ID
     */
	private Long id;
	/**
	 *  手机号
     */
	private String phone;
	/**
	 *  验证码
     */
	private String smsCode;
	/**
	 *  有效期
     */
	private Integer expireSecond;
	/**
	 *  发送时间
     */
	private Date sendTime;
	/**
	 *  创建人ID
     */
	private Long createUserId;
	/**
	 *  创建人名称
     */
	private String createUserName;
	/**
	 *  修改人ID
     */
	private Long updateUserId;
	/**
	 *  修改人名称
     */
	private String updateUserName;
	/**
	 *  是否删除 1：已删除 0：未删除
     */
	private Integer isDel;
}
