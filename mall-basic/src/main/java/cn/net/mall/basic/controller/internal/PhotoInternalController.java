package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.entity.common.CommonPhotoConditionEntity;
import cn.net.mall.basic.entity.common.CommonPhotoEntity;
import cn.net.mall.basic.service.common.CommonPhotoService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 图片 内部接口层（Feign 调用）
 *
 * @date 2024-07-03 16:43:09
 */
@Tag(name = "内部服务-图片管理", description = "内部Feign调用：图片资源管理")
@RestController
@RequestMapping("/v1/internal/commonPhoto")
public class PhotoInternalController {

    private final CommonPhotoService commonPhotoService;

    public PhotoInternalController(CommonPhotoService commonPhotoService) {
        this.commonPhotoService = commonPhotoService;
    }

    /**
     * 通过id查询图片信息
     *
     * @param id 系统ID
     * @return 图片信息
     */
    @Operation(summary = "通过id查询图片信息", description = "内部Feign调用：根据主键查询图片")
    @GetMapping("/findById")
    public CommonPhotoEntity findById(Long id) {
        return commonPhotoService.findById(id);
    }

    /**
     * 根据条件查询图片列表
     *
     * @param commonPhotoConditionEntity 条件
     * @return 图片列表
     */
    @Operation(summary = "分页查询图片（管理端）", description = "分页查询图片列表，支持按图片名称、分组、上传时间等条件筛选")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonPhotoEntity> searchByPage(@RequestBody CommonPhotoConditionEntity commonPhotoConditionEntity) {
        return commonPhotoService.searchByPage(commonPhotoConditionEntity);
    }


    /**
     * 添加图片
     *
     * @param commonPhotoEntity 图片实体
     * @return 影响行数
     */
    @Operation(summary = "新增图片（管理端）", description = "新增图片记录，关联已上传的图片文件")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonPhotoEntity commonPhotoEntity) {
        if (Objects.isNull(commonPhotoEntity.getPhotoGroupId())) {
            //默认分组
            commonPhotoEntity.setPhotoGroupId(0L);
        }
        return commonPhotoService.insert(commonPhotoEntity);
    }

    /**
     * 修改图片
     *
     * @param commonPhotoEntity 图片实体
     * @return 影响行数
     */
    @Operation(summary = "修改图片（管理端）", description = "修改图片记录信息，如图片名称、所属分组等")
    @PostMapping("/update")
    public int update(@RequestBody CommonPhotoEntity commonPhotoEntity) {
        return commonPhotoService.update(commonPhotoEntity);
    }

    /**
     * 批量删除图片
     *
     * @param ids 图片ID集合
     * @return 影响行数
     */
    @Operation(summary = "删除图片（管理端）", description = "根据ID列表批量删除图片记录")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonPhotoService.deleteByIds(ids);
    }
}
