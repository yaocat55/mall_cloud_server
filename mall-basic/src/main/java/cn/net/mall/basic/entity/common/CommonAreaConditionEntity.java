package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.RequestConditionEntity;
import lombok.Data;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 地区查询条件实体
 *
 * @date 2024-10-04 11:43:55
 */
@Data
@Schema(description = "行政区域")

public class CommonAreaConditionEntity extends RequestConditionEntity {

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
	 *  上一级ID
     */
	@Schema(description = "上级ID")
	private Long parentId;
	/**
	 *  名称
     */
	@Schema(description = "名称")
	private String name;
	/**
	 *  拼音
     */
	@Schema(description = "pinyin")
	private String pinyin;
	/**
	 *  全称
     */
	@Schema(description = "full Name")
	private String fullName;
	/**
	 *  行政编码
     */
	@Schema(description = "编码")
	private String code;
	/**
	 *  级别
     */
	@Schema(description = "等级")
	private Integer level;
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
