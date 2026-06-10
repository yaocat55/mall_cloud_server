package cn.net.mall.basic.controller.common;

import cn.net.mall.basic.entity.common.CommonPhotoGroupConditionEntity;
import cn.net.mall.basic.entity.common.CommonPhotoGroupEntity;
import cn.net.mall.basic.service.common.CommonPhotoGroupService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图片分组 接口层
 *
 * @date 2024-07-03 16:43:09
 */
@Tag(name = "图片分组操作", description = "图片分组接口")
@RestController
@RequestMapping("/v1/commonPhotoGroup")
public class CommonPhotoGroupController {

    private final CommonPhotoGroupService commonPhotoGroupService;

    public CommonPhotoGroupController(CommonPhotoGroupService commonPhotoGroupService) {
        this.commonPhotoGroupService = commonPhotoGroupService;
    }

    /**
     * 通过id查询图片分组信息
     *
     * @param id 系统ID
     * @return 图片分组信息
     */
    @Operation(summary = "通过id查询图片分组信息", description = "通过id查询图片分组信息")
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
    @Operation(summary = "根据条件查询图片分组列表", description = "根据条件查询图片分组列表")
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
    @Operation(summary = "添加图片分组", description = "添加图片分组")
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
    @Operation(summary = "修改图片分组", description = "修改图片分组")
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
    @Operation(summary = "批量删除图片分组", description = "批量删除图片分组")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonPhotoGroupService.deleteByIds(ids);
    }
}
