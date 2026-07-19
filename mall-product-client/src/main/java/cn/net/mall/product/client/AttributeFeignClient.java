package cn.net.mall.product.client;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.AttributeDTO;
import cn.net.mall.product.dto.AttributeConditionDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

import static cn.net.mall.product.constant.AppConstant.PRODUCT_SERVICE_NAME;

/**
 * 属性 Feign 客户端
 *
 * @date 2025/07/03
 */
@FeignClient(value = PRODUCT_SERVICE_NAME, contextId = "attributeFeignClient")
public interface AttributeFeignClient {

    @Operation(summary = "分页查询属性", description = "按条件分页查询属性列表，请求参数包含 pageNo、pageSize 及可选筛选条件（如属性名称、属性类型等）")
    @PostMapping("/v1/attribute/searchByPage")
    ResponsePageEntity<?> searchByPage(@RequestBody AttributeConditionDTO condition);

    @Operation(summary = "新增属性", description = "新增属性记录，请求体包含属性名称、属性类型、排序等字段")
    @PostMapping("/v1/internal/attribute/insert")
    int insert(@RequestBody AttributeDTO entity);

    @Operation(summary = "修改属性", description = "修改属性记录，根据 ID 更新属性名称、属性类型、排序等字段")
    @PostMapping("/v1/internal/attribute/update")
    int update(@RequestBody AttributeDTO entity);

    @Operation(summary = "删除属性", description = "根据 ID 列表批量删除属性记录")
    @PostMapping("/v1/internal/attribute/deleteByIds")
    int deleteByIds(@RequestBody @NotNull List<Long> ids);
}
