package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.BrandConditionEntity;
import cn.net.mall.product.entity.BrandEntity;
import cn.net.mall.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌 接口层
 *
 * @date 2024-05-09 14:43:55
 */
@Tag(name = "品牌操作", description = "品牌接口")
@RestController
@RequestMapping("/v1/brand")
public class BrandController {

	private final BrandService brandService;

	public BrandController(BrandService brandService) {
		this.brandService = brandService;
	}

	/**
	 * 通过id查询品牌信息
	 *
	 * @param id 系统ID
	 * @return 品牌信息
	 */
	@Operation(summary = "通过id查询品牌信息", description =  "通过id查询品牌信息")
	@GetMapping("/findById")
	public BrandEntity findById(Long id) {
		return brandService.findById(id);
	}

	/**
    * 根据条件查询品牌列表
    *
    * @param brandConditionEntity 条件
    * @return 品牌列表
    */
	@Operation(summary = "根据条件查询品牌列表", description =  "根据条件查询品牌列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<BrandEntity> searchByPage(@RequestBody BrandConditionEntity brandConditionEntity) {
		return brandService.searchByPage(brandConditionEntity);
	}


	/**
     * 添加品牌
     *
     * @param brandEntity 品牌实体
     * @return 影响行数
     */
	@Operation(summary = "添加品牌", description =  "添加品牌")
	@PostMapping("/insert")
	public int insert(@RequestBody BrandEntity brandEntity) {
		return brandService.insert(brandEntity);
	}

	/**
     * 修改品牌
     *
     * @param brandEntity 品牌实体
     * @return 影响行数
     */
	@Operation(summary = "修改品牌", description =  "修改品牌")
	@PostMapping("/update")
	public int update(@RequestBody BrandEntity brandEntity) {
		return brandService.update(brandEntity);
	}

	/**
     * 批量删除品牌
     *
     * @param ids 品牌ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除品牌", description =  "批量删除品牌")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return brandService.deleteByIds(ids);
	}

	/**
	 * 导出品牌数据
	 *
	 * @return 影响行数
	 */
	@Operation(summary = "导出品牌数据", description =  "导出品牌数据")
	@PostMapping("/export")
	public void export(@RequestBody BrandConditionEntity brandConditionEntity) {
	}
}
