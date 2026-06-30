package cn.net.mall.admin.controller.admin;

import cn.net.mall.basic.client.AreaFeignClient;
import cn.net.mall.basic.client.DictFeignClient;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/v1/common")
@RequiredArgsConstructor
@Tag(name = "管理后台-基础数据", description = "区域、字典等基础数据管理接口，需携带 Bearer Token")
public class AdminCommonController {
    private final AreaFeignClient areaFeignClient;
    private final DictFeignClient dictFeignClient;

    @Operation(summary = "分页查询区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/page")
    public ResponsePageEntity<?> searchAreaPage(@RequestBody Map<String, Object> c) { return null; }
    @Operation(summary = "新增区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/insert")
    public int insertArea(@RequestBody Object e) { return 0; }
    @Operation(summary = "修改区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/update")
    public int updateArea(@RequestBody Object e) { return 0; }
    @Operation(summary = "删除区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/delete")
    public int deleteArea(@RequestBody @NotNull List<Long> ids) { return 0; }

    @Operation(summary = "分页查询字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/page")
    public ResponsePageEntity<?> searchDictPage(@RequestBody Map<String, Object> c) { return null; }
    @Operation(summary = "新增字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/insert")
    public int insertDict(@RequestBody Object e) { return 0; }
    @Operation(summary = "修改字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/update")
    public int updateDict(@RequestBody Object e) { return 0; }
    @Operation(summary = "删除字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/delete")
    public int deleteDict(@RequestBody @NotNull List<Long> ids) { return 0; }
}