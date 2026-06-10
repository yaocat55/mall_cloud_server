package cn.net.mall.product.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品组属性实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-09-07 17:28:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductGroupAttributeEntity extends BaseEntity {


	/**
	 * 商品组ID
	 */
	private Long productGroupId;

	/**
	 * 属性ID
	 */
	private Long attributeId;

	/**
	 * 属性值ID
	 */
	private Long attributeValueId;
}
