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

    @Operation(summary = "分页查询区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/page")
    public ResponsePageEntity<?> searchAreaPage(@RequestBody Map<String, Object> c) { return areaFeignClient.searchByPage(c); }
    @Operation(summary = "新增区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/insert")
    public int insertArea(@RequestBody Object e) { return areaFeignClient.insert(e); }
    @Operation(summary = "修改区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/update")
    public int updateArea(@RequestBody Object e) { return areaFeignClient.update(e); }
    @Operation(summary = "删除区域", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/area/delete")
    public int deleteArea(@RequestBody @NotNull List<Long> ids) { return areaFeignClient.deleteByIds(ids); }

    @Operation(summary = "分页查询字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/page")
    public ResponsePageEntity<?> searchDictPage(@RequestBody Map<String, Object> c) { return dictFeignClient.searchByPage(c); }
    @Operation(summary = "新增字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/insert")
    public int insertDict(@RequestBody Object e) { return dictFeignClient.insert(e); }
    @Operation(summary = "修改字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/update")
    public int updateDict(@RequestBody Object e) { return dictFeignClient.update(e); }
    @Operation(summary = "删除字典", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/dict/delete")
    public int deleteDict(@RequestBody @NotNull List<Long> ids) { return dictFeignClient.deleteByIds(ids); }
// ========== 文件上传 ==========    @Operation(summary = "上传图片", description = "上传单张图片到 MinIO 文件服务器，返回可访问的图片 URL", security = @SecurityRequirement(name = "Bearer Token"))    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)    public FileDTO uploadImage(@Parameter(description = "图片文件（支持 jpg/png/gif/webp 格式）") MultipartFile file) throws Exception {        return uploadFeignClient.imageUpload(file);    }
}