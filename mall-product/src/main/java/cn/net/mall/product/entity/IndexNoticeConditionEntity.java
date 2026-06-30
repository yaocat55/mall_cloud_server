package cn.net.mall.product.entity;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 首页公告查询条件实体
 *
 * @date 2024-10-03 15:58:40
 */
@Data
@Schema(description = "首页公告")

public class IndexNoticeConditionEntity extends RequestConditionEntity {

   /**
    * ID集合
    */
    private List<Long> idList;

	/**
	 *  ID
     */
	private Long id;
	/**
	 *  标题
     */
	private String title;
	/**
	 *  内容
     */
	private String content;
	/**
	 *  排序
     */
	private Integer sort;
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
