package cn.net.mall.basic.entity.common;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片分组实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-07-03 16:43:09
 */
@Schema(name ="图片分组实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonPhotoGroupEntity extends BaseEntity {


	/**
	 * 分组名称
	 */
	@Schema(name ="分组名称")
	private String name;
}
