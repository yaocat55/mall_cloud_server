package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.entity.common.CommonDictDetailEntity;
import cn.net.mall.basic.service.common.CommonDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部字典接口
 * <p>
 * <b>调用方：</b>所有模块通过 Feign 调用，查询字典明细数据
 */
@Tag(name = "内部服务-字典", description = "内部微服务：所有模块通过 Feign 调用，查询字典数据")
@RestController
@RequestMapping("/v1/internal/dict")
public class DictInternalController {
    private final CommonDictService commonDictService;

    public DictInternalController(CommonDictService commonDictService) {
        this.commonDictService = commonDictService;
    }

    @Operation(summary = "根据字典名称查询字典明细",
               description = "内部服务：由各模块通过 Feign 调用，根据字典名称获取字典明细列表")
    @GetMapping("/queryDictDetail")
    public List<CommonDictDetailEntity> queryDictDetail(@RequestParam String dictName) {
        return commonDictService.queryDictDetailEntity(dictName);
    }
}
