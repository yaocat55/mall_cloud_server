package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务查询条件实体
 *
 * @date 2024-04-30 15:09:06
 */
@Schema(name ="定时任务查询条件实体")
@Data
public class CommonJobConditionEntity extends RequestConditionEntity {


	/**
	 *  ID
     */
	@Schema(name ="ID")
	private Long id;

	/**
	 *  定时任务名称
     */
	@Schema(name ="定时任务名称")
	private String jobName;

	/**
	 *  暂停状态 0：未暂停 1：已暂停
     */
	@Schema(name ="暂停状态 0：未暂停 1：已暂停")
	private Integer pauseStatus;

	/**
	 *  bean名称
     */
	@Schema(name ="bean名称")
	private String beanName;

	/**
	 *  方法名称
     */
	@Schema(name ="方法名称")
	private String methodName;

	/**
	 *  cron 表达式
     */
	@Schema(name ="cron 表达式")
	private String cronExpression;

	/**
	 *  参数
     */
	@Schema(name ="参数")
	private String params;

	/**
	 *  备注
     */
	@Schema(name ="备注")
	private String remark;

	/**
	 *  创建人ID
     */
	@Schema(name ="创建人ID")
	private Long createUserId;

	/**
	 *  创建人名称
     */
	@Schema(name ="创建人名称")
	private String createUserName;

	/**
	 *  创建日期
     */
	@Schema(name ="创建日期")
	private Date createTime;

	/**
	 *  修改人ID
     */
	@Schema(name ="修改人ID")
	private Long updateUserId;

	/**
	 *  修改人名称
     */
	@Schema(name ="修改人名称")
	private String updateUserName;

	/**
	 *  修改时间
     */
	@Schema(name ="修改时间")
	private Date updateTime;

	/**
	 *  是否删除 1：已删除 0：未删除
     */
	@Schema(name ="是否删除 1：已删除 0：未删除")
	private Integer isDel;
}
