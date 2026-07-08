package cn.net.mall.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 首页公告Web实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-10-03 15:58:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "首页公告")

public class IndexNoticeDTO {

	/**
	 * 系统编号
	 */
	@Schema(description = "系统ID", example = "0")
	private Long id;

	/**
	 * 标题
	 */
	@Schema(description = "标题", example = "string")
	private String title;

	/**
	 * 发布时间
	 */
	@Schema(description = "创建时间", example = "string")
	private String createTime;

	/**
	 * 排序
	 */
	@Schema(description = "排序", example = "0")
	private Integer sort;
}
