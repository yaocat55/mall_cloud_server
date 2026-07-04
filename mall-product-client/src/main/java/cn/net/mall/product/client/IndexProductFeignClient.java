package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 首页商品 Feign 客户端
 *
 * @date 2025/07/03
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "indexProductFeignClient")
public interface IndexProductFeignClient {

    @Operation(summary = "分页查询首页商品", description = "按条件分页查询首页商品列表，请求参数包含 pageNo、pageSize 及可选筛选条件（如商品ID、排序等）")
    @PostMapping("/v1/indexProduct/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody Map<String, Object> condition);

    @Operation(summary = "新增首页商品", description = "新增首页商品记录，请求体包含商品ID、排序、状态等字段")
    @PostMapping("/v1/indexProduct/insert")
    int insert(@RequestBody Object entity);

    @Operation(summary = "修改首页商品", description = "修改首页商品记录，根据 ID 更新商品ID、排序、状态等字段")
    @PostMapping("/v1/indexProduct/update")
    int update(@RequestBody Object entity);

    @Operation(summary = "删除首页商品", description = "根据 ID 列表批量删除首页商品记录")
    @PostMapping("/v1/indexProduct/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
