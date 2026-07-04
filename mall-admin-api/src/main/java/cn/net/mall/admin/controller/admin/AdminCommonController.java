package cn.net.mall.admin.controller.admin;

import cn.net.mall.basic.client.AreaFeignClient;
import cn.net.mall.basic.client.DictFeignClient;
import cn.net.mall.basic.client.UploadFeignClient;
import cn.net.mall.basic.dto.FileDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final UploadFeignClient uploadFeignClient;

    @Operation(summary = "分页查询区域", description = "多条件分页查询行政区域列表，支持按名称、层级等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/page")
    public ResponsePageEntity<?> searchAreaPage(@RequestBody Map<String, Object> c) { return areaFeignClient.searchByPage(c); }
    @Operation(summary = "新增区域", description = "新增一条行政区域记录，包含区域名称、父区域、层级等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/insert")
    public int insertArea(@RequestBody Object e) { return areaFeignClient.insert(e); }
    @Operation(summary = "修改区域", description = "修改已有行政区域的信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/update")
    public int updateArea(@RequestBody Object e) { return areaFeignClient.update(e); }
    @Operation(summary = "删除区域", description = "根据 ID 列表批量删除行政区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/delete")
    public int deleteArea(@RequestBody @NotNull List<Long> ids) { return areaFeignClient.deleteByIds(ids); }

    @Operation(summary = "分页查询字典", description = "多条件分页查询数据字典列表，支持按字典类型等条件筛选", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/page")
    public ResponsePageEntity<?> searchDictPage(@RequestBody Map<String, Object> c) { return dictFeignClient.searchByPage(c); }
    @Operation(summary = "新增字典", description = "新增一条数据字典记录，包含字典编码、字典名称、字典值等", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/insert")
    public int insertDict(@RequestBody Object e) { return dictFeignClient.insert(e); }
    @Operation(summary = "修改字典", description = "修改已有数据字典的信息", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/update")
    public int updateDict(@RequestBody Object e) { return dictFeignClient.update(e); }
    @Operation(summary = "删除字典", description = "根据 ID 列表批量删除数据字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/delete")
    public int deleteDict(@RequestBody @NotNull List<Long> ids) { return dictFeignClient.deleteByIds(ids); }

    // ========== 文件上传 ==========

    @Operation(summary = "上传图片",
               description = "上传单张图片到 MinIO 文件服务器，返回包含可访问 URL 的文件信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO uploadImage(@Parameter(description = "图片文件（支持 jpg/png/gif/webp 格式）") MultipartFile file) throws Exception {
        return uploadFeignClient.imageUpload(file);
    }
}