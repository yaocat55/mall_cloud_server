package cn.net.mall.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页公告Web实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-10-03 15:58:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndexNoticeDTO {

	/**
	 * 系统编号
	 */
	private Long id;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 发布时间
	 */
	private String createTime;

	/**
	 * 排序
	 */
	private Integer sort;
}
