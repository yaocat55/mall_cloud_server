package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.entity.common.CommonPhotoGroupConditionEntity;
import cn.net.mall.basic.entity.common.CommonPhotoGroupEntity;
import cn.net.mall.basic.service.common.CommonPhotoGroupService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图片分组 内部接口层（Feign 调用）
 *
 * @date 2024-07-03 16:43:09
 */
@Tag(name = "内部服务-图片分组管理", description = "内部Feign调用：图片分组管理")
@RestController
@RequestMapping("/v1/internal/commonPhotoGroup")
public class PhotoGroupInternalController {

    private final CommonPhotoGroupService commonPhotoGroupService;

    public PhotoGroupInternalController(CommonPhotoGroupService commonPhotoGroupService) {
        this.commonPhotoGroupService = commonPhotoGroupService;
    }

    /**
     * 通过id查询图片分组信息
     *
     * @param id 系统ID
     * @return 图片分组信息
     */
    @Operation(summary = "通过id查询图片分组信息", description = "内部Feign调用：根据主键查询图片分组")
    @GetMapping("/findById")
    public CommonPhotoGroupEntity findById(Long id) {
        return commonPhotoGroupService.findById(id);
    }

    /**
     * 根据条件查询图片分组列表
     *
     * @param commonPhotoGroupConditionEntity 条件
     * @return 图片分组列表
     */
    @Operation(summary = "分页查询图片分组（管理端）", description = "分页查询图片分组列表，支持按分组名称等条件筛选")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonPhotoGroupEntity> searchByPage(@RequestBody CommonPhotoGroupConditionEntity commonPhotoGroupConditionEntity) {
        return commonPhotoGroupService.searchByPage(commonPhotoGroupConditionEntity);
    }


    /**
     * 添加图片分组
     *
     * @param commonPhotoGroupEntity 图片分组实体
     * @return 影响行数
     */
    @Operation(summary = "新增图片分组（管理端）", description = "新增图片分组，用于对图片进行分类管理")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonPhotoGroupEntity commonPhotoGroupEntity) {
        return commonPhotoGroupService.insert(commonPhotoGroupEntity);
    }

    /**
     * 修改图片分组
     *
     * @param commonPhotoGroupEntity 图片分组实体
     * @return 影响行数
     */
    @Operation(summary = "修改图片分组（管理端）", description = "修改图片分组信息，如分组名称、排序等")
    @PostMapping("/update")
    public int update(@RequestBody CommonPhotoGroupEntity commonPhotoGroupEntity) {
        return commonPhotoGroupService.update(commonPhotoGroupEntity);
    }

    /**
     * 批量删除图片分组
     *
     * @param ids 图片分组ID集合
     * @return 影响行数
     */
    @Operation(summary = "删除图片分组（管理端）", description = "根据ID列表批量删除图片分组")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonPhotoGroupService.deleteByIds(ids);
    }
}
