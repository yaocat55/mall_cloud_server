package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 地区实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-10-04 11:43:55
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "行政区域")

public class CommonAreaEntity extends BaseEntity {


	/**
	 * 上一级ID
	 */
	@Schema(description = "上级ID")
	private Long parentId;

	/**
	 * 名称
	 */
	@Schema(description = "名称")
	private String name;

	/**
	 * 拼音
	 */
	@Schema(description = "pinyin")
	private String pinyin;

	/**
	 * 全称
	 */
	@Schema(description = "full Name")
	private String fullName;

	/**
	 * 行政编码
	 */
	@Schema(description = "编码")
	private String code;

	/**
	 * 级别
	 */
	@Schema(description = "等级")
	private Integer level;
}
