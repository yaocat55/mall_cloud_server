package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 首页公告实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-10-03 15:58:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "首页公告")

public class IndexNoticeEntity extends BaseEntity {


	/**
	 * 标题
	 */
	private String title;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 排序
	 */
	private Integer sort;
}
