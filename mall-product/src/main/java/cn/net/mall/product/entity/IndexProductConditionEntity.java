package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 首页商品查询条件实体
 *
 * @date 2024-08-27 17:37:52
 */
@Schema(description = "首页商品查询条件实体")
@Data
public class IndexProductConditionEntity extends RequestConditionEntity {


	/**
	 *  ID
     */
	@Schema(description = "ID", example = "1")
	private Long id;

	/**
	 *  商品ID
     */
	@Schema(description = "商品ID", example = "1")
	private Long productId;

	/**
	 *  排序
     */
	@Schema(description = "排序", example = "1")
	private Integer sort;

	/**
	 *  商品类型 1: 热门商品 2: 最新商品 3：秒杀商品
     */
	@Schema(description = "商品类型 1: 热门商品 2: 最新商品 3：秒杀商品", example = "1")
	private Integer type;

	/**
	 *  创建人ID
     */
	@Schema(description = "创建人ID", example = "1")
	private Long createUserId;

	/**
	 *  创建人名称
     */
	@Schema(description = "创建人名称", example = "admin")
	private String createUserName;

	/**
	 *  创建日期
     */
	@Schema(description = "创建日期", example = "2024-01-01 00:00:00")
	private Date createTime;

	/**
	 *  修改人ID
     */
	@Schema(description = "修改人ID", example = "1")
	private Long updateUserId;

	/**
	 *  修改人名称
     */
	@Schema(description = "修改人名称", example = "admin")
	private String updateUserName;

	/**
	 *  修改时间
     */
	@Schema(description = "修改时间", example = "2024-01-01 00:00:00")
	private Date updateTime;

	/**
	 *  是否删除 1：已删除 0：未删除
     */
	@Schema(description = "是否删除 1：已删除 0：未删除", example = "0")
	private Integer isDel;
}
