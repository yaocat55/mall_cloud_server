package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 首页轮播图查询条件实体
 *
 * @date 2024-08-21 18:34:11
 */
@Schema(name = "首页轮播图查询条件实体")
@Data
public class IndexCarouselImageConditionEntity extends RequestConditionEntity {


	/**
	 *  ID
     */
	@Schema(name = "ID")
	private Long id;

	/**
	 *  图片url
     */
	@Schema(name = "图片url")
	private String url;

	/**
	 *  排序
     */
	@Schema(name = "排序")
	private Integer sort;

	/**
	 *  创建人ID
     */
	@Schema(name = "创建人ID")
	private Long createUserId;

	/**
	 *  创建人名称
     */
	@Schema(name = "创建人名称")
	private String createUserName;

	/**
	 *  创建日期
     */
	@Schema(name = "创建日期")
	private Date createTime;

	/**
	 *  修改人ID
     */
	@Schema(name = "修改人ID")
	private Long updateUserId;

	/**
	 *  修改人名称
     */
	@Schema(name = "修改人名称")
	private String updateUserName;

	/**
	 *  修改时间
     */
	@Schema(name = "修改时间")
	private Date updateTime;

	/**
	 *  是否删除 1：已删除 0：未删除
     */
	@Schema(name = "是否删除 1：已删除 0：未删除")
	private Integer isDel;
}
