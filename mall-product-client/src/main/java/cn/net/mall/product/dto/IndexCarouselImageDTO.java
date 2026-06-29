package cn.net.mall.product.dto;

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
@Schema(description = "首页轮播图实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndexCarouselImageDTO extends BaseEntity {


	/**
	 * 图片url
	 */
	@Schema(description = "图片url", example = "https://example.com/image.png")
	private String url;

	/**
	 * 排序
	 */
	@Schema(description = "排序", example = "1")
	private Integer sort;
}
