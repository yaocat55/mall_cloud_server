package cn.net.mall.basic.controller.common;

import cn.net.mall.basic.entity.common.CommonDictConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictEntity;
import cn.net.mall.basic.service.common.CommonDictService;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典 接口层
 *
 * @date 2024-03-21 18:50:46
 */
@Tag(name = "字典管理", description = "管理后台：数据字典配置")
@RestController
@RequestMapping("/v1/dict")
public class CommonDictController {

    private final CommonDictService commonDictService;

    public CommonDictController(CommonDictService commonDictService) {
        this.commonDictService = commonDictService;
    }

    /**
     * 通过id查询数据字典信息
     *
     * @param id 系统ID
     * @return 数据字典信息
     */
    @Operation(summary = "通过id查询数据字典信息", description =  "通过id查询数据字典信息")
    @GetMapping("/findById")
    public CommonDictEntity findById(Long id) {
        return commonDictService.findById(id);
    }

    /**
     * 根据条件查询数据字典列表
     *
     * @param commonDictConditionEntity 条件
     * @return 数据字典列表
     */
    @Operation(summary = "根据条件查询数据字典列表", description =  "根据条件查询数据字典列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonDictEntity> searchByPage(@RequestBody CommonDictConditionEntity commonDictConditionEntity) {
        return commonDictService.searchByPage(commonDictConditionEntity);
    }


    /**
     * 添加数据字典
     *
     * @param dictEntity 数据字典实体
     * @return 影响行数
     */
    @Operation(summary = "添加数据字典", description =  "添加数据字典")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonDictEntity dictEntity) {
        return commonDictService.insert(dictEntity);
    }

    /**
     * 修改数据字典
     *
     * @param dictEntity 数据字典实体
     * @return 影响行数
     */
    @Operation(summary = "修改数据字典", description =  "修改数据字典")
    @PostMapping("/update")
    public int update(@RequestBody CommonDictEntity dictEntity) {
        return commonDictService.update(dictEntity);
    }

    /**
     * 删除数据字典
     *
     * @param ids 数据字典ID
     * @return 影响行数
     */
    @Operation(summary = "删除数据字典", description =  "删除数据字典")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonDictService.deleteByIds(ids);
    }
}
