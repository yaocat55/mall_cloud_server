package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.UnitConditionEntity;
import cn.net.mall.product.entity.UnitEntity;
import cn.net.mall.product.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单位 接口层
 *
 * @date 2024-05-09 14:43:55
 */
@Tag(name = "单位管理", description = "管理后台：商品单位配置")
@RestController
@RequestMapping("/v1/unit")
public class UnitController {

	private final UnitService unitService;

	public UnitController(UnitService unitService) {
		this.unitService = unitService;
	}

	/**
	 * 通过id查询单位信息
	 *
	 * @param id 系统ID
	 * @return 单位信息
	 */
	@Operation(summary = "通过id查询单位信息", description =  "通过id查询单位信息")
	@GetMapping("/findById")
	public UnitEntity findById(Long id) {
		return unitService.findById(id);
	}

	/**
    * 根据条件查询单位列表
    *
    * @param unitConditionEntity 条件
    * @return 单位列表
    */
	@Operation(summary = "根据条件查询单位列表", description =  "根据条件查询单位列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<UnitEntity> searchByPage(@RequestBody UnitConditionEntity unitConditionEntity) {
		return unitService.searchByPage(unitConditionEntity);
	}


	/**
     * 添加单位
     *
     * @param unitEntity 单位实体
     * @return 影响行数
     */
	@Operation(summary = "添加单位", description =  "添加单位")
	@PostMapping("/insert")
	public int insert(@RequestBody UnitEntity unitEntity) {
		return unitService.insert(unitEntity);
	}

	/**
     * 修改单位
     *
     * @param unitEntity 单位实体
     * @return 影响行数
     */
	@Operation(summary = "修改单位", description =  "修改单位")
	@PostMapping("/update")
	public int update(@RequestBody UnitEntity unitEntity) {
		return unitService.update(unitEntity);
	}

	/**
     * 批量删除单位
     *
     * @param ids 单位ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除单位", description =  "批量删除单位")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return unitService.deleteByIds(ids);
	}

	/**
	 * 导出单位数据
	 *
	 * @return 影响行数
	 */
	@Operation(summary = "导出岗位数据", description =  "导出单位数据")
	@PostMapping("/export")
	public void export(@RequestBody UnitConditionEntity unitConditionEntity) {
	}
}
