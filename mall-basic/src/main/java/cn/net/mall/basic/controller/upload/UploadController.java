package cn.net.mall.basic.controller.upload;

import cn.net.mall.annotation.NoLogin;
import cn.net.mall.basic.dto.FileDTO;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.basic.service.upload.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static cn.net.mall.basic.service.upload.UploadService.FILE;
import static cn.net.mall.basic.service.upload.UploadService.IMAGE;


/**
 * 上传文件
 *
 * @date 2024/5/5 下午5:25
 */
@AllArgsConstructor
@Slf4j
@Tag(name = "文件上传", description = "管理后台：文件上传/下载")
@RestController
@RequestMapping("/v1")
public class UploadController {

    private final UploadService uploadService;

    @Operation(summary = "批量上传图片接口", description = "批量上传图片接口")
    @PostMapping(value = "/image/batchUpload")
    public FileDTO batchUpload(@Parameter(description = "上传文件")
    @RequestParam("file") MultipartFile[] files) {
        try {
            return uploadService.batchUpload(files);
        } catch (Exception e) {
            log.info("批量上传图片失败，原因：", e);
            throw new BusinessException("批量上传图片失败，请稍后重试");
        }
    }

    @NoLogin
    @Operation(summary = "上传文件接口", description = "上传文件接口")
    @PostMapping(value = "/file/upload")
    public FileDTO fileUpload(MultipartFile file) throws Exception {
        return uploadService.upload(file, FILE, null);
    }

    @NoLogin
    @Operation(summary = "上传图片接口", description = "上传图片接口")
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDTO imageUpload(MultipartFile file) throws Exception {
        return uploadService.upload(file, IMAGE, null);
    }
}
