package cn.net.mall.product.controller.internal;

import cn.net.mall.product.entity.UnitEntity;
import cn.net.mall.product.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部单位接口
 *
* **调用方：**
 *   - admin-bff（管理后台）— 单位 CRUD 操作
 *
* **不对外暴露**，仅限服务间 Feign 调用
 */
@Tag(name = "内部服务-单位", description = "内部微服务：admin-bff 通过 Feign 调用")
@RestController
@RequestMapping("/v1/internal/unit")
public class UnitInternalController {

    private final UnitService unitService;

    public UnitInternalController(UnitService unitService) {
        this.unitService = unitService;
    }

    @Operation(summary = "新增单位",
               description = "内部服务：由 admin-bff 通过 Feign 调用，新增单位记录")
    @PostMapping("/insert")
    public int insert(@RequestBody UnitEntity unitEntity) {
        return unitService.insert(unitEntity);
    }

    @Operation(summary = "修改单位",
               description = "内部服务：由 admin-bff 通过 Feign 调用，修改单位信息")
    @PostMapping("/update")
    public int update(@RequestBody UnitEntity unitEntity) {
        return unitService.update(unitEntity);
    }

    @Operation(summary = "批量删除单位",
               description = "内部服务：由 admin-bff 通过 Feign 调用，根据ID集合批量删除单位")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return unitService.deleteByIds(ids);
    }
}
