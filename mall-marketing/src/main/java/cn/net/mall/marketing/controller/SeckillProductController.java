package cn.net.mall.marketing.controller;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.SeckillProductConditionEntity;
import cn.net.mall.marketing.entity.SeckillProductEntity;
import cn.net.mall.marketing.service.SeckillProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 秒杀商品 接口层
 *
 * @date 2024-07-08 10:57:31
 */
@AllArgsConstructor
@Tag(name = "秒杀商品操作", description = "秒杀商品操作")
@RestController
@RequestMapping("/v1/seckillProduct")
public class SeckillProductController {

	private final SeckillProductService seckillProductService;

	/**
	 * 通过id查询秒杀商品信息
	 *
	 * @param id 系统ID
	 * @return 秒杀商品信息
	 */
	@Operation(summary = "通过id查询秒杀商品信息", description = "通过id查询秒杀商品信息")
	@GetMapping("/findById")
	public SeckillProductEntity findById(Long id) {
		return seckillProductService.findById(id);
	}

	/**
    * 根据条件查询秒杀商品列表
    *
    * @param seckillProductConditionEntity 条件
    * @return 秒杀商品列表
    */
	@Operation(summary = "根据条件查询秒杀商品列表", description = "根据条件查询秒杀商品列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<SeckillProductEntity> searchByPage(@RequestBody SeckillProductConditionEntity seckillProductConditionEntity) {
		return seckillProductService.searchByPage(seckillProductConditionEntity);
	}


	/**
     * 添加秒杀商品
     *
     * @param seckillProductEntity 秒杀商品实体
     * @return 影响行数
     */
	@Operation(summary = "添加秒杀商品", description = "添加秒杀商品")
	@PostMapping("/insert")
	public void insert(@RequestBody SeckillProductEntity seckillProductEntity) {
		 seckillProductService.insert(seckillProductEntity);
	}

	/**
     * 修改秒杀商品
     *
     * @param seckillProductEntity 秒杀商品实体
     * @return 影响行数
     */
	@Operation(summary = "修改秒杀商品", description = "修改秒杀商品")
	@PostMapping("/update")
	public void update(@RequestBody SeckillProductEntity seckillProductEntity) {
		seckillProductService.update(seckillProductEntity);
	}

	/**
     * 批量删除秒杀商品
     *
     * @param ids 秒杀商品ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除秒杀商品", description = "批量删除秒杀商品")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return seckillProductService.deleteByIds(ids);
	}
}
