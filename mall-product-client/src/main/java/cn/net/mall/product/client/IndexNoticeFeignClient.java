package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 首页公告 Feign 客户端
 *
 * @date 2025/07/03
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "indexNoticeFeignClient")
public interface IndexNoticeFeignClient {

    @Operation(summary = "分页查询首页公告", description = "按条件分页查询首页公告列表，请求参数包含 pageNo、pageSize 及可选筛选条件（如公告标题、状态等）")
    @PostMapping("/v1/indexNotice/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增首页公告", description = "新增首页公告记录，请求体包含公告标题、内容、排序、状态等字段")
    @PostMapping("/v1/indexNotice/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改首页公告", description = "修改首页公告记录，根据 ID 更新公告标题、内容、排序、状态等字段")
    @PostMapping("/v1/indexNotice/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除首页公告", description = "根据 ID 列表批量删除首页公告记录")
    @PostMapping("/v1/indexNotice/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);

    @Operation(summary = "查询首页公告详情", description = "根据ID查询首页公告详细内容")
    @GetMapping("/v1/indexNotice/findById")
    Object findById(@RequestParam("id") Long id);
}
