package cn.net.mall.basic.controller.common;

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
 * 图片 接口层
 *
 * @date 2024-07-03 16:43:09
 */
@Tag(name = "图片操作", description = "图片接口")
@RestController
@RequestMapping("/v1/commonPhoto")
public class CommonPhotoController {

    private final CommonPhotoService commonPhotoService;

    public CommonPhotoController(CommonPhotoService commonPhotoService) {
        this.commonPhotoService = commonPhotoService;
    }

    /**
     * 通过id查询图片信息
     *
     * @param id 系统ID
     * @return 图片信息
     */
    @Operation(summary = "通过id查询图片信息", description = "通过id查询图片信息")
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
    @Operation(summary = "根据条件查询图片列表", description = "根据条件查询图片列表")
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
    @Operation(summary = "添加图片", description = "添加图片")
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
    @Operation(summary = "修改图片", description = "修改图片")
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
    @Operation(summary = "批量删除图片", description = "批量删除图片")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonPhotoService.deleteByIds(ids);
    }
}
