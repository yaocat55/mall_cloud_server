package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页轮播图实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-08-21 18:34:11
 */
@Schema(name = "首页轮播图实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndexCarouselImageEntity extends BaseEntity {


	/**
	 * 图片url
	 */
	@Schema(name = "图片url")
	private String url;

	/**
	 * 排序
	 */
	@Schema(name = "排序")
	private Integer sort;
}
