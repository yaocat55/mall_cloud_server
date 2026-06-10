package cn.net.mall.product.controller.product;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.entity.AttributeValueConditionEntity;
import cn.net.mall.product.entity.AttributeValueEntity;
import cn.net.mall.product.service.AttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性值 接口层
 *
 * @date 2024-05-09 14:43:55
 */
@Tag(name = "属性值操作", description = "属性值接口")
@RestController
@RequestMapping("/v1/attributeValue")
public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    public AttributeValueController(AttributeValueService attributeValueService) {
        this.attributeValueService = attributeValueService;
    }

    /**
     * 通过id查询属性值信息
     *
     * @param id 系统ID
     * @return 属性值信息
     */
    @Operation(summary = "通过id查询属性值信息", description =  "通过id查询属性值信息")
    @GetMapping("/findById")
    public AttributeValueEntity findById(Long id) {
        return attributeValueService.findById(id);
    }

    /**
     * 根据条件查询属性值列表
     *
     * @param attributeValueConditionEntity 条件
     * @return 属性值列表
     */
    @Operation(summary = "根据条件查询属性值列表", description =  "根据条件查询属性值列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<AttributeValueEntity> searchByPage(@RequestBody AttributeValueConditionEntity attributeValueConditionEntity) {
        return attributeValueService.searchByPage(attributeValueConditionEntity);
    }


    /**
     * 添加属性值
     *
     * @param attributeValueEntity 属性值实体
     * @return 影响行数
     */
    @Operation(summary = "添加属性值", description =  "添加属性值")
    @PostMapping("/insert")
    public int insert(@RequestBody AttributeValueEntity attributeValueEntity) {
        return attributeValueService.insert(attributeValueEntity);
    }

    /**
     * 修改属性值
     *
     * @param attributeValueEntity 属性值实体
     * @return 影响行数
     */
    @Operation(summary = "修改属性值", description =  "修改属性值")
    @PostMapping("/update")
    public int update(@RequestBody AttributeValueEntity attributeValueEntity) {
        return attributeValueService.update(attributeValueEntity);
    }

    /**
     * 批量删除属性值
     *
     * @param ids 属性值ID集合
     * @return 影响行数
     */
    @Operation(summary = "批量删除属性值", description =  "批量删除属性值")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return attributeValueService.deleteByIds(ids);
    }

    /**
     * 导出属性值数据
     *
     * @return 影响行数
     */
    @Operation(summary = "导出属性值数据", description =  "导出属性值数据")
    @PostMapping("/export")
    public void export(@RequestBody AttributeValueConditionEntity attributeValueConditionEntity) {
    }
}
