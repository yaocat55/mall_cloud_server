package cn.net.mall.product.controller.product;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.CategoryTreeDTO;
import cn.net.mall.product.entity.CategoryConditionEntity;
import cn.net.mall.product.entity.CategoryEntity;
import cn.net.mall.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类 接口层
 *
 * @date 2024-05-09 14:43:55
 */
@Tag(name = "分类操作", description = "分类接口")
@RestController
@RequestMapping("/v1/category")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * 通过id查询分类信息
	 *
	 * @param id 系统ID
	 * @return 分类信息
	 */
	@Operation(summary = "通过id查询分类信息", description =  "通过id查询分类信息")
	@GetMapping("/findById")
	public CategoryEntity findById(Long id) {
		return categoryService.findById(id);
	}

	/**
    * 根据条件查询分类列表
    *
    * @param categoryConditionEntity 条件
    * @return 分类列表
    */
	@Operation(summary = "根据条件查询分类列表", description =  "根据条件查询分类列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<CategoryEntity> searchByPage(@RequestBody CategoryConditionEntity categoryConditionEntity) {
		return categoryService.searchByPage(categoryConditionEntity);
	}


	/**
     * 添加分类
     *
     * @param categoryEntity 分类实体
     * @return 影响行数
     */
	@Operation(summary = "添加分类", description =  "添加分类")
	@PostMapping("/insert")
	public void insert(@RequestBody CategoryEntity categoryEntity) {
		categoryService.insert(categoryEntity);
	}

	/**
     * 修改分类
     *
     * @param categoryEntity 分类实体
     * @return 影响行数
     */
	@Operation(summary = "修改分类", description =  "修改分类")
	@PostMapping("/update")
	public int update(@RequestBody CategoryEntity categoryEntity) {
		return categoryService.update(categoryEntity);
	}

	/**
     * 批量删除分类
     *
     * @param ids 分类ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除分类", description =  "批量删除分类")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return categoryService.deleteByIds(ids);
	}

	/**
	 * 查询分类树
	 *
	 * @param categoryConditionEntity 条件
	 * @return 分类树
	 */
	@NoLogin
	@Operation(summary = "查询分类树", description =  "查询分类树")
	@PostMapping("/searchByTree")
	public List<CategoryTreeDTO> searchByTree(@RequestBody CategoryConditionEntity categoryConditionEntity) {
		return categoryService.searchByTree(categoryConditionEntity);
	}

	/**
	 * 导出分类数据
	 *
	 * @return 影响行数
	 */
	@Operation(summary = "导出分类数据", description =  "导出分类数据")
	@PostMapping("/export")
	public void export(@RequestBody CategoryConditionEntity categoryConditionEntity) {
	}
}
