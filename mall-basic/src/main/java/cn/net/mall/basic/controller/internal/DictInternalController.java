package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.service.common.CommonDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "内部服务-字典", description = "内部微服务：所有模块通过 Feign 调用，根据 code 获取字典值")
@RestController
@RequestMapping("/v1/dict")
public class DictInternalController {
    private final CommonDictService commonDictService;
    public DictInternalController(CommonDictService commonDictService) {
        this.commonDictService = commonDictService;
    }
    @Operation(summary = "根据编码查询字典", description = "内部服务：由各模块通过 Feign 调用，根据字典编码获取字典值列表")
    @GetMapping("/getByCode")
    public Object getByCode(@RequestParam String code) {
        return commonDictService.getByCode(code);
    }
}
