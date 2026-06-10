package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.IndexNoticeConditionEntity;
import cn.net.mall.product.entity.IndexNoticeEntity;
import cn.net.mall.product.service.IndexNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页公告 接口层
 *
 * @date 2024-10-03 15:58:40
 */
@AllArgsConstructor
@RestController
@RequestMapping("/v1/indexNotice")
public class IndexNoticeController {

	private final IndexNoticeService indexNoticeService;

	/**
	 * 通过id查询首页公告信息
	 *
	 * @param id 系统ID
	 * @return 首页公告信息
	 */
	@Operation(summary = "通过id查询首页公告信息", description = "通过id查询首页公告信息")
	@GetMapping("/findById")
	public IndexNoticeEntity findById(Long id) {
		return indexNoticeService.findById(id);
	}

	/**
    * 根据条件查询首页公告列表
    *
    * @param indexNoticeConditionEntity 条件
    * @return 首页公告列表
    */
	@Operation(summary = "根据条件查询首页公告列表", description = "根据条件查询首页公告列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<IndexNoticeEntity> searchByPage(@RequestBody IndexNoticeConditionEntity indexNoticeConditionEntity) {
		return indexNoticeService.searchByPage(indexNoticeConditionEntity);
	}


	/**
     * 添加首页公告
     *
     * @param indexNoticeEntity 首页公告实体
     * @return 影响行数
     */
	@Operation(summary = "添加首页公告", description = "添加首页公告")
	@PostMapping("/insert")
	public int insert(@RequestBody IndexNoticeEntity indexNoticeEntity) {
		return indexNoticeService.insert(indexNoticeEntity);
	}

	/**
     * 修改首页公告
     *
     * @param indexNoticeEntity 首页公告实体
     * @return 影响行数
     */
	@Operation(summary = "修改首页公告", description = "修改首页公告")
	@PostMapping("/update")
	public int update(@RequestBody IndexNoticeEntity indexNoticeEntity) {
		return indexNoticeService.update(indexNoticeEntity);
	}

	/**
     * 批量删除首页公告
     *
     * @param ids 首页公告ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除首页公告", description = "批量删除首页公告")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return indexNoticeService.deleteByIds(ids);
	}
}
