package cn.net.mall.basic.controller.common;

import cn.net.mall.basic.entity.common.CommonSensitiveWordConditionEntity;
import cn.net.mall.basic.entity.common.CommonSensitiveWordEntity;
import cn.net.mall.basic.service.common.CommonSensitiveWordService;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 敏感词 接口层
 *
 * @date 2024-05-18 21:09:00
 */
@Tag(name = "敏感词管理", description = "管理后台：敏感词过滤配置")
@RestController
@RequestMapping("/v1/commonSensitiveWord")
public class CommonSensitiveWordController {

    private final CommonSensitiveWordService commonSensitiveWordService;

    public CommonSensitiveWordController(CommonSensitiveWordService commonSensitiveWordService) {
        this.commonSensitiveWordService = commonSensitiveWordService;
    }

    /**
     * 校验敏感词
     *
     * @param commonSensitiveWordEntity 条件
     * @return 敏感词信息
     */
    @Operation(summary = "校验敏感词", description = "校验敏感词")
    @PostMapping("/checkSensitiveWord")
    public void checkSensitiveWord(@RequestBody CommonSensitiveWordEntity commonSensitiveWordEntity) {
        AssertUtil.isTrue(StringUtils.hasLength(commonSensitiveWordEntity.getWord()), "word字段不能为空");
        commonSensitiveWordService.checkSensitiveWord(commonSensitiveWordEntity.getWord());
    }


    /**
     * 通过id查询敏感词信息
     *
     * @param id 系统ID
     * @return 敏感词信息
     */
    @Operation(summary = "通过id查询敏感词信息", description = "通过id查询敏感词信息")
    @GetMapping("/findById")
    public CommonSensitiveWordEntity findById(Long id) {
        return commonSensitiveWordService.findById(id);
    }

    /**
     * 根据条件查询敏感词列表
     *
     * @param commonSensitiveWordConditionEntity 条件
     * @return 敏感词列表
     */
    @Operation(summary = "根据条件查询敏感词列表", description = "根据条件查询敏感词列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonSensitiveWordEntity> searchByPage(@RequestBody CommonSensitiveWordConditionEntity commonSensitiveWordConditionEntity) {
        return commonSensitiveWordService.searchByPage(commonSensitiveWordConditionEntity);
    }


    /**
     * 添加敏感词
     *
     * @param commonSensitiveWordEntity 敏感词实体
     * @return 影响行数
     */
    @Operation(summary = "添加敏感词", description = "添加敏感词")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonSensitiveWordEntity commonSensitiveWordEntity) {
        return commonSensitiveWordService.insert(commonSensitiveWordEntity);
    }

    /**
     * 修改敏感词
     *
     * @param commonSensitiveWordEntity 敏感词实体
     * @return 影响行数
     */
    @Operation(summary = "修改敏感词", description = "修改敏感词")
    @PostMapping("/update")
    public int update(@RequestBody CommonSensitiveWordEntity commonSensitiveWordEntity) {
        return commonSensitiveWordService.update(commonSensitiveWordEntity);
    }

    /**
     * 批量删除敏感词
     *
     * @param ids 敏感词ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除敏感词", description = "批量删除敏感词")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonSensitiveWordService.deleteByIds(ids);
    }
}
