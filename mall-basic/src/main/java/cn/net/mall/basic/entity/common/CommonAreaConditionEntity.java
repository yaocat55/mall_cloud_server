package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;

/**
 * 地区查询条件实体
 *
 * @date 2024-10-04 11:43:55
 */
@Data
public class CommonAreaConditionEntity extends RequestConditionEntity {

   /**
    * ID集合
    */
    private List<Long> idList;

	/**
	 *  ID
     */
	private Long id;
	/**
	 *  上一级ID
     */
	private Long parentId;
	/**
	 *  名称
     */
	private String name;
	/**
	 *  拼音
     */
	private String pinyin;
	/**
	 *  全称
     */
	private String fullName;
	/**
	 *  行政编码
     */
	private String code;
	/**
	 *  级别
     */
	private Integer level;
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
