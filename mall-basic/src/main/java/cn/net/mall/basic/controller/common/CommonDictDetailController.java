package cn.net.mall.basic.controller.common;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.basic.dto.DictDetailConditionDTO;
import cn.net.mall.basic.dto.DictDetailDTO;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailEntity;
import cn.net.mall.basic.service.common.CommonDictDetailService;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门 接口层
 *
 * @date 2024-03-25 21:41:03
 */
@Tag(name = "部门操作", description = "部门接口")
@RestController
@RequestMapping("/v1/dictDetail")
public class CommonDictDetailController {

    private final CommonDictDetailService commonDictDetailService;

    public CommonDictDetailController(CommonDictDetailService commonDictDetailService) {
        this.commonDictDetailService = commonDictDetailService;
    }

    /**
     * 通过id查询数据字典详情信息
     *
     * @param id 系统ID
     * @return 数据字典详情信息
     */
    @Operation(summary = "通过id查询数据字典详情信息", description = "通过id查询数据字典详情信息")
    @GetMapping("/findById")
    public CommonDictDetailEntity findById(Long id) {
        return commonDictDetailService.findById(id);
    }

    /**
     * 根据条件查询数据字典详情列表
     *
     * @param commonDictDetailConditionEntity 条件
     * @return 数据字典详情列表
     */
    @Operation(summary = "根据条件查询数据字典详情列表", description = "根据条件查询数据字典详情列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<CommonDictDetailEntity> searchByPage(@RequestBody CommonDictDetailConditionEntity commonDictDetailConditionEntity) {
        return commonDictDetailService.searchByPage(commonDictDetailConditionEntity);
    }


    /**
     * 根据条件查询数据字典详情列表
     *
     * @param dictDetailConditionDTO 条件
     * @return 数据字典详情列表
     */
    @Operation(summary = "根据条件查询数据字典详情列表", description = "根据条件查询数据字典详情列表")
    @PostMapping("/searchDictDetail")
    public List<DictDetailDTO> searchDictDetail(@RequestBody @NotNull DictDetailConditionDTO dictDetailConditionDTO) {
        CommonDictDetailConditionEntity commonDictDetailConditionEntity = BeanUtil.toBean(dictDetailConditionDTO, CommonDictDetailConditionEntity.class);
        List<CommonDictDetailEntity> dictDetailEntities = commonDictDetailService.searchDictDetailFromCache(commonDictDetailConditionEntity);
        if (CollectionUtils.isEmpty(dictDetailEntities)) {
            return Lists.newArrayList();
        }
        return BeanUtil.copyToList(dictDetailEntities, DictDetailDTO.class);
    }

    /**
     * 添加数据字典详情
     *
     * @param dictDetailEntity 数据字典详情实体
     * @return 影响行数
     */
    @Operation(summary = "添加数据字典详情", description = "添加数据字典详情")
    @PostMapping("/insert")
    public int insert(@RequestBody CommonDictDetailEntity dictDetailEntity) {
        return commonDictDetailService.insert(dictDetailEntity);
    }

    /**
     * 修改数据字典详情
     *
     * @param dictDetailEntity 数据字典详情实体
     * @return 影响行数
     */
    @Operation(summary = "修改数据字典详情", description = "修改数据字典详情")
    @PostMapping("/update")
    public int update(@RequestBody CommonDictDetailEntity dictDetailEntity) {
        return commonDictDetailService.update(dictDetailEntity);
    }

    /**
     * 删除数据字典详情
     *
     * @param ids 数据字典详情ID
     * @return 影响行数
     */
    @Operation(summary = "删除数据字典详情", description = "删除数据字典详情")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return commonDictDetailService.deleteByIds(ids);
    }
}
