package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.AttributeValueEntity;
import cn.net.mall.product.service.AttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部属性值接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 属性值 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-属性值", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/attributeValue")
public class AttributeValueInternalController {

    private final AttributeValueService attributeValueService;

    public AttributeValueInternalController(AttributeValueService attributeValueService) {
        this.attributeValueService = attributeValueService;
    }

    @Operation(summary = "新增属性值",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增属性值记录")
    @PostMapping("/insert")
    public int insert(@RequestBody AttributeValueEntity attributeValueEntity) {
        return attributeValueService.insert(attributeValueEntity);
    }

    @Operation(summary = "修改属性值",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改属性值信息")
    @PostMapping("/update")
    public int update(@RequestBody AttributeValueEntity attributeValueEntity) {
        return attributeValueService.update(attributeValueEntity);
    }

    @Operation(summary = "批量删除属性值",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除属性值")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return attributeValueService.deleteByIds(ids);
    }
}
