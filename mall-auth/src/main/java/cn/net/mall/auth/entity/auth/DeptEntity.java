package cn.net.mall.auth.entity.auth;

import cn.net.mall.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部门实体 该项目是知识星球：java突击队 的内部项目
 *
 * @date 2024-01-08 17:18:17
 */
@Schema(description = "部门实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeptEntity extends BaseEntity {


	/**
	 * 名称
	 */
	@ExcelProperty(value = "部门名称", index = 0)
	@Schema(description = "名称", example = "测试数据")
	private String name;

	/**
	 * 上级部门
	 */
	@ExcelProperty(value = "上级部门ID", index = 1)
	@Schema(description = "上级部门", example = "0")
	private Long pid;

	/**
	 * 有效状态 1:有效 0:无效
	 */
	@ExcelProperty(value = "有效状态", index = 2)
	@Schema(description = "有效状态 1:有效 0:无效", example = "1")
	private Boolean validStatus;

	/**
	 * 角色ID
	 */
	private Long roleId;
}
