package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.AttributeConditionEntity;
import cn.net.mall.product.entity.AttributeEntity;
import cn.net.mall.product.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性 接口层
 *
 * @date 2024-05-09 14:43:55
 */
@Tag(name = "商品属性管理", description = "管理后台：商品属性配置")
@RestController
@RequestMapping("/v1/attribute")
public class AttributeController {

	private final AttributeService attributeService;

	public AttributeController(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * 通过id查询属性信息
	 *
	 * @param id 系统ID
	 * @return 属性信息
	 */
	@Operation(summary = "通过id查询属性信息", description = "通过id查询属性信息")
	@GetMapping("/findById")
	public AttributeEntity findById(Long id) {
		return attributeService.findById(id);
	}

	/**
    * 根据条件查询属性列表
    *
    * @param attributeConditionEntity 条件
    * @return 属性列表
    */
	@Operation(summary = "根据条件查询属性列表", description =  "根据条件查询属性列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<AttributeEntity> searchByPage(@RequestBody AttributeConditionEntity attributeConditionEntity) {
		return attributeService.searchByPage(attributeConditionEntity);
	}


	/**
     * 添加属性
     *
     * @param attributeEntity 属性实体
     * @return 影响行数
     */
	@Operation(summary = "添加属性", description =  "添加属性")
	@PostMapping("/insert")
	public int insert(@RequestBody AttributeEntity attributeEntity) {
		return attributeService.insert(attributeEntity);
	}

	/**
     * 修改属性
     *
     * @param attributeEntity 属性实体
     * @return 影响行数
     */
	@Operation(summary = "修改属性", description =  "修改属性")
	@PostMapping("/update")
	public int update(@RequestBody AttributeEntity attributeEntity) {
		return attributeService.update(attributeEntity);
	}

	/**
     * 批量删除属性
     *
     * @param ids 属性ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除属性", description =  "批量删除属性")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return attributeService.deleteByIds(ids);
	}

	/**
	 * 导出属性数据
	 *
	 * @return 影响行数
	 */
	@Operation(summary = "导出属性数据", description =  "导出属性数据")
	@PostMapping("/export")
	public void export(@RequestBody AttributeConditionEntity attributeConditionEntity) {
	}
}
