package cn.net.mall.basic.controller.common;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonAreaConditionEntity;
import cn.net.mall.basic.entity.common.CommonAreaEntity;
import cn.net.mall.basic.service.common.CommonAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地区 接口层
 *
 * @date 2024-10-04 11:43:55
 */
import cn.net.mall.basic.dto.AreaDTO;

// ... existing code ...

@Tag(name = "地区接口层", description = "地区接口层")
@RestController
@RequestMapping("/v1/commonArea")
public class CommonAreaController {

	private final CommonAreaService commonAreaService;

	public CommonAreaController(CommonAreaService commonAreaService) {
		this.commonAreaService = commonAreaService;
	}

    /**
     * 根据parentId获取地区列表
     *
     * @param parentId 上级地区ID
     * @return 地区列表
     */
    @Operation(summary = "根据parentId获取地区列表", description = "根据parentId获取地区列表")
    @GetMapping("/queryByParentId")
    public List<AreaDTO> queryByParentId(@RequestParam Long parentId) {
        return commonAreaService.getAreaByParentId(parentId);
    }

	/**
	 * 通过id查询地区信息
	 *
	 * @param id 系统ID
	 * @return 地区信息
	 */
	@Operation(summary = "通过id查询地区信息", description = "通过id查询地区信息")
	@GetMapping("/findById")
	public CommonAreaEntity findById(Long id) {
		return commonAreaService.findById(id);
	}

	/**
    * 根据条件查询地区列表
    *
    * @param commonAreaConditionEntity 条件
    * @return 地区列表
    */
	@Operation(summary = "根据条件查询地区列表", description = "根据条件查询地区列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<CommonAreaEntity> searchByPage(@RequestBody CommonAreaConditionEntity commonAreaConditionEntity) {
		return commonAreaService.searchByPage(commonAreaConditionEntity);
	}


	/**
     * 添加地区
     *
     * @param commonAreaEntity 地区实体
     * @return 影响行数
     */
	@Operation(summary = "添加地区", description = "添加地区")
	@PostMapping("/insert")
	public int insert(@RequestBody CommonAreaEntity commonAreaEntity) {
		return commonAreaService.insert(commonAreaEntity);
	}

	/**
     * 修改地区
     *
     * @param commonAreaEntity 地区实体
     * @return 影响行数
     */
	@Operation(summary = "修改地区", description = "修改地区")
	@PostMapping("/update")
	public int update(@RequestBody CommonAreaEntity commonAreaEntity) {
		return commonAreaService.update(commonAreaEntity);
	}

	/**
     * 批量删除地区
     *
     * @param ids 地区ID集合
     * @return 影响行数
     */
	@Operation(summary = "批量删除地区", description = "批量删除地区")
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return commonAreaService.deleteByIds(ids);
	}
}
