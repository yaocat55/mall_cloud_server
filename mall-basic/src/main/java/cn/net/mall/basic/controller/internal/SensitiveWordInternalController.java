package cn.net.mall.basic.controller.internal;

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
 * 敏感词 内部接口层（Feign 调用）
 *
 * @date 2024-05-18 21:09:00
 */
@Tag(name = "内部服务-敏感词管理", description = "内部Feign调用：敏感词过滤配置管理")
@RestController
@RequestMapping("/v1/internal/commonSensitiveWord")
public class SensitiveWordInternalController {

    private final CommonSensitiveWordService commonSensitiveWordService;

    public SensitiveWordInternalController(CommonSensitiveWordService commonSensitiveWordService) {
        this.commonSensitiveWordService = commonSensitiveWordService;
    }

    /**
     * 校验敏感词
     *
     * @param commonSensitiveWordEntity 条件
     * @return 敏感词信息
     */
    @Operation(summary = "校验敏感词", description = "校验文本内容中是否包含敏感词，返回校验结果")
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
    @Operation(summary = "通过id查询敏感词信息", description = "内部Feign调用：根据主键查询敏感词")
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
    @Operation(summary = "分页查询敏感词（管理端）", description = "分页查询敏感词列表，支持按敏感词内容等条件筛选")
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
    @Operation(summary = "新增敏感词（管理端）", description = "新增敏感词，添加到敏感词过滤库")
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
    @Operation(summary = "修改敏感词（管理端）", description = "修改敏感词内容或状态")
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
    @Operation(summary = "删除敏感词（管理端）", description = "根据ID列表批量删除敏感词")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonSensitiveWordService.deleteByIds(ids);
    }
}
