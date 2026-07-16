package cn.net.mall.admin.controller.admin;

import cn.net.mall.basic.client.UploadFeignClient;
import cn.net.mall.basic.dto.FileDTO;
import cn.net.mall.util.ApiResult;
import cn.net.mall.util.ApiResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/admin/v1/common")
@RequiredArgsConstructor
@Tag(name = "基础数据", description = "图片库、敏感词、通知、文件上传")
public class AdminCommonController {
    private final UploadFeignClient uploadFeignClient;

    // ========== 文件上传 ==========

    @Operation(summary = "上传图片",
               description = "上传单张图片到 MinIO 文件服务器，返回包含可访问 URL 的文件信息",
               security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<FileDTO> uploadImage(@Parameter(description = "图片文件（支持 jpg/png/gif/webp 格式）") MultipartFile file) throws Exception {
        return ApiResultUtil.success(uploadFeignClient.imageUpload(file));
    }
}
