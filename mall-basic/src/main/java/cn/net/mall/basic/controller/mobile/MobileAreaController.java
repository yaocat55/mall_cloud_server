package cn.net.mall.basic.controller.mobile;

import cn.net.mall.basic.dto.AreaDTO;
import cn.net.mall.basic.service.common.CommonAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地区接口
 *
 * @date 2024-10-04
 */
@Tag(name = "地区接口", description = "地区接口")
@RestController
@RequestMapping("/v1/mobile/area")
@RequiredArgsConstructor
public class MobileAreaController {

    private final CommonAreaService commonAreaService;

    /**
     * 根据parentId获取地区列表
     *
     * @param parentId 上级地区ID
     * @return 地区列表
     */
    @Operation(summary = "根据parentId获取地区列表", description = "根据parentId获取地区列表")
    @GetMapping("/queryByParentId")
    public List<AreaDTO> queryByParentId(@RequestParam Long parentId) {
        return commonAreaService.getAreaByParentId(parentId);
    }
}
