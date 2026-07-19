package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.AttributeEntity;
import cn.net.mall.product.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部属性接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 属性 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-属性", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/attribute")
public class AttributeInternalController {

    private final AttributeService attributeService;

    public AttributeInternalController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Operation(summary = "新增属性",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增属性记录")
    @PostMapping("/insert")
    public int insert(@RequestBody AttributeEntity attributeEntity) {
        return attributeService.insert(attributeEntity);
    }

    @Operation(summary = "修改属性",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改属性信息")
    @PostMapping("/update")
    public int update(@RequestBody AttributeEntity attributeEntity) {
        return attributeService.update(attributeEntity);
    }

    @Operation(summary = "批量删除属性",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除属性")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return attributeService.deleteByIds(ids);
    }
}
