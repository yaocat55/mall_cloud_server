package cn.net.mall.basic.controller.internal;

import cn.net.mall.basic.dto.AreaDTO;
import cn.net.mall.basic.service.common.CommonAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "内部服务-地区", description = "内部微服务：mall-order、mall-product、mall-auth 通过 Feign 调用")
@RestController
@RequestMapping("/v1/commonArea")
public class AreaInternalController {
    private final CommonAreaService commonAreaService;
    public AreaInternalController(CommonAreaService commonAreaService) {
        this.commonAreaService = commonAreaService;
    }
    @Operation(summary = "根据 parentId 查询地区列表", description = "内部服务：由各模块通过 Feign 调用，根据父级ID获取子级地区列表")
    @GetMapping("/queryByParentId")
    public List<AreaDTO> queryByParentId(@RequestParam Long parentId) {
        return commonAreaService.getAreaByParentId(parentId);
    }
    @Operation(summary = "根据ID查询地区", description = "内部服务：由各模块通过 Feign 调用，根据ID获取地区详情")
    @GetMapping("/findById")
    public Object findById(Long id) {
        return commonAreaService.findById(id);
    }
}
