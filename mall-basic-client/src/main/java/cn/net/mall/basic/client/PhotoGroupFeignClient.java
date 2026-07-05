package cn.net.mall.basic.client;

import cn.net.mall.basic.dto.CommonPhotoGroupConditionDTO;
import cn.net.mall.basic.dto.CommonPhotoGroupDTO;
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
*   - mall-admin-api（管理后台）— 图片分组管理
 * 
*
 * @date 2025/7/3
 */
@FeignClient(value = BASIC_SERVICE_NAME, contextId = "photoGroupFeignClient")
public interface PhotoGroupFeignClient {

    @Operation(summary = "通过id查询图片分组信息", description = "内部Feign调用：根据主键查询图片分组")
    @GetMapping("/v1/commonPhotoGroup/findById")
    Object findById(@RequestParam("id") Long id);

    @Operation(summary = "分页查询图片分组（管理端）", description = "分页查询图片分组列表，支持按分组名称等条件筛选")
    @PostMapping("/v1/commonPhotoGroup/searchByPage")
    ResponsePageEntity<CommonPhotoGroupDTO> searchByPage(@RequestBody CommonPhotoGroupConditionDTO condition);

    @Operation(summary = "新增图片分组（管理端）", description = "新增图片分组，用于对图片进行分类管理")
    @PostMapping("/v1/commonPhotoGroup/insert")
    int insert(@RequestBody CommonPhotoGroupDTO entity);

    @Operation(summary = "修改图片分组（管理端）", description = "修改图片分组信息，如分组名称、排序等")
    @PostMapping("/v1/commonPhotoGroup/update")
    int update(@RequestBody CommonPhotoGroupDTO entity);

    @Operation(summary = "删除图片分组（管理端）", description = "根据ID列表批量删除图片分组")
    @PostMapping("/v1/commonPhotoGroup/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
