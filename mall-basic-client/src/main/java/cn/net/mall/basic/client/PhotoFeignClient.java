package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonPhotoConditionDTO;
import cn.net.mall.basic.dto.CommonPhotoDTO;
import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static cn.net.mall.basic.constant.AppConstant.BASIC_SERVICE_NAME;

/**
 * [Service] Feign 客户端
 * 
* **调用方：**
 * 
*   - mall-admin-api（管理后台）— 图片资源管理
 * 
*
 * @date 2025/7/3
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "photoFeignClient")
public interface PhotoFeignClient {

    @Operation(summary = "通过id查询图片信息", description = "内部Feign调用：根据主键查询图片")
    @GetMapping("/v1/internal/commonPhoto/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "分页查询图片（管理端）", description = "分页查询图片列表，支持按图片名称、分组、上传时间等条件筛选")
    @PostMapping("/v1/internal/commonPhoto/searchByPage")
    ResponsePageEntity<CommonPhotoDTO> searchByPage(@RequestBody CommonPhotoConditionDTO condition);

    @Operation(summary = "新增图片（管理端）", description = "新增图片记录，关联已上传的图片文件")
    @PostMapping("/v1/internal/commonPhoto/insert")
    int insert(@RequestBody CommonPhotoDTO entity);

    @Operation(summary = "修改图片（管理端）", description = "修改图片记录信息，如图片名称、所属分组等")
    @PostMapping("/v1/internal/commonPhoto/update")
    int update(@RequestBody CommonPhotoDTO entity);

    @Operation(summary = "删除图片（管理端）", description = "根据ID列表批量删除图片记录")
    @PostMapping("/v1/internal/commonPhoto/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
