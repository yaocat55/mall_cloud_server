package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.FileDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * 
* **调用方：**
 * 
*   - mall-auth（权限服务）— 上传文件/图片
 *   - mall-product（商品服务）— 上传文件/图片
 * 
*
 * @date 2025/5/27 17:09
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "uploadFeignClient")
public interface UploadFeignClient {

    @Operation(summary = "上传图片接口", description = "内部Feign调用：上传图片至文件服务器")
    @PostMapping(value = "/v1/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileDTO imageUpload(MultipartFile file) throws Exception;
}
