package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.AttributeConditionEntity;
import cn.net.mall.product.entity.ProductAttributeConditionEntity;
import cn.net.mall.product.entity.ProductAttributeEntity;
import cn.net.mall.product.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品属性 接口层
 *
 * @date 2024-05-09 14:43:56
 */
@Tag(name = "商品属性操作", description = "商品属性接口")
@RestController
@RequestMapping("/v1/productAttribute")
public class ProductAttributeController {

	private final ProductAttributeService productAttributeService;

	public ProductAttributeController(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	/**
	 * 通过id查询商品属性信息
	 *
	 * @param id 系统ID
	 * @return 商品属性信息
	 */
	@Operation(summary = "通过id查询商品属性信息", description =  "通过id查询商品属性信息")
	@GetMapping("/findById")
	public ProductAttributeEntity findById(Long id) {
		return productAttributeService.findById(id);
	}

	/**
    * 根据条件查询商品属性列表
    *
    * @param productAttributeConditionEntity 条件
    * @return 商品属性列表
    */
	@Operation(summary = "根据条件查询商品属性列表", description =  "根据条件查询商品属性列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<ProductAttributeEntity> searchByPage(@RequestBody ProductAttributeConditionEntity productAttributeConditionEntity) {
		return productAttributeService.searchByPage(productAttributeConditionEntity);
	}


	/**
     * 添加商品属性
     *
     * @param productAttributeEntity 商品属性实体
     * @return 影响行数
     */
	@Operation(summary = "添加商品属性", description =  "添加商品属性")
	@PostMapping("/insert")
	public int insert(@RequestBody ProductAttributeEntity productAttributeEntity) {
		return productAttributeService.insert(productAttributeEntity);
	}

	/**
     * 修改商品属性
     *
     * @param productAttributeEntity 商品属性实体
     * @return 影响行数
     */
	@Operation(summary = "修改商品属性", description =  "修改商品属性")
	@PostMapping("/update")
	public int update(@RequestBody ProductAttributeEntity productAttributeEntity) {
		return productAttributeService.update(productAttributeEntity);
	}

	/**
     * 批量删除商品属性
     *
     * @param ids 商品属性ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除商品属性", description =  "批量删除商品属性")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return productAttributeService.deleteByIds(ids);
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
