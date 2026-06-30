package cn.net.mall.marketing.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 优惠券实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "优惠券")

public class CouponEntity extends BaseEntity {


	/**
	 * 优惠券名称
	 */
	private String name;

	/**
	 * 类型 1：现金券 2：阶梯满减 3：每满减 4：通用折扣 5：满N件折扣 6：满Y元折扣
	 */
	private Integer type;

	/**
	 * 图片地址
	 */
	private String photoUrl;

	/**
	 * 领券开始时间
	 */
	private Date receiveStartTime;

	/**
	 * 领券结束时间
	 */
	private Date receiveEndTime;

	/**
	 * 使用开始时间
	 */
	private Date useStartTime;

	/**
	 * 使用结束时间
	 */
	private Date useEndTime;

	/**
	 * 优惠券数量
	 */
	private Integer quantity;

	/**
	 * 优惠金额，比如：满100，减40， 这里就是40
	 */
	private Integer offMoney;

	/**
	 * 折扣，百分之多少，比如：8折，就填入80
	 */
	private Integer discount;

	/**
	 * 最低使用金额，比如：满100，减40， 这里就是100
	 */
	private Integer minMoney;

	/**
	 * 最少商品件数，比如：2件或者3件
	 */
	private Integer minProductCount;

	/**
	 * 每日限额，每天领取多少张优惠券
	 */
	private Integer limitCountOneDay;

	/**
	 * 有效状态 1:有效 0:无效
	 */
	private Integer validStatus;
}
