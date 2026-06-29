package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户头像实体 该项目是知识星球：java突击队 的内部项目
 * 
 * @date 2024-01-08 17:18:18
 */
@Schema(description = "用户头像实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAvatarEntity extends BaseEntity {
	

	/**
	 * 文件名
	 */
	@Schema(description = "文件名", example = "-")
	private String fileName;

	/**
	 * 路径
	 */
	@Schema(description = "路径", example = "-")
	private String path;

	/**
	 * 大小
	 */
	@Schema(description = "大小", example = "-")
	private String fileSize;
}
